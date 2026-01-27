package me.bannock.donutguard.obf.job;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import me.bannock.donutguard.obf.asm.JarHandler;
import me.bannock.donutguard.obf.asm.JarHandlerFactory;
import me.bannock.donutguard.obf.asm.entry.FileEntry;
import me.bannock.donutguard.obf.asm.entry.impl.ClassEntry;
import me.bannock.donutguard.obf.asm.entry.impl.DummyEntry;
import me.bannock.donutguard.obf.asm.entry.impl.ResourceEntry;
import me.bannock.donutguard.obf.config.Configuration;
import me.bannock.donutguard.obf.config.DefaultConfigGroup;
import me.bannock.donutguard.obf.filter.RegexMapFilter;
import me.bannock.donutguard.obf.mutator.Mutator;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This class handles the code behind the obfuscation. It creates and uses the mutators.
 */
public class ObfuscatorJob implements Runnable {

    private final Logger logger = LogManager.getLogger();
    private String threadId = "Obfuscator Job " + System.currentTimeMillis() + "." + System.nanoTime(); // This will be replaced by the obfuscator
    private final Configuration configuration;
    private final Module[] jobModulePlugins;
    private final InputStream jarInputStream;

    private boolean hasStarted = false, failed = false;

    private JarHandler jarHandler;

    @AssistedInject
    public ObfuscatorJob(@Assisted Configuration configuration,
                         @Assisted Module[] jobModulePlugins,
                         JarHandlerFactory jarHandlerFactory){
        this.configuration = SerializationUtils.clone(configuration);
        this.jarInputStream = null;
        this.jobModulePlugins = jobModulePlugins;
        this.jarHandler = jarHandlerFactory.create(configuration);
    }

    @AssistedInject
    public ObfuscatorJob(@Assisted Configuration configuration,
                         @Assisted Module[] jobModulePlugins,
                         @Assisted InputStream jarInputStream,
                         JarHandlerFactory jarHandlerFactory){
        this.configuration = SerializationUtils.clone(configuration);
        this.jarInputStream = jarInputStream;
        this.jobModulePlugins = jobModulePlugins;
        this.jarHandler = jarHandlerFactory.create(configuration);
    }

    @Override
    public void run() {
        ThreadContext.remove("threadId");
        ThreadContext.put("threadId", threadId);
        hasStarted = true;

        try {
            runObfuscator();
        }
        catch (Throwable e) {
            if (!(e instanceof InterruptedException)) {
                failed = true;
                logger.error("An error occurred while running the obfuscator", e);
                throw new RuntimeException(e);
            }else{
                logger.info("Obfuscation job interrupted. Likely cancelled by user.");
            }
        }

        ThreadContext.remove("threadId");
    }

    /**
     * Runs the obfuscator
     * @throws Exception If an error occurs while running the obfuscator
     */
    private void runObfuscator() throws Exception {
        logger.info("Starting obfuscation job...");

        logger.info("Creating job injector...");
        List<Module> guiceModules = new ArrayList<>();
        guiceModules.add(new JobModule(this));
        guiceModules.addAll(Arrays.asList(jobModulePlugins));
        Injector injector = Guice.createInjector(guiceModules.toArray(new Module[0]));
        logger.info("Successfully created job injector");
        logger.info("Creating and loading jar handler...");
        this.jarHandler = injector.getInstance(JarHandler.class);
        if (jarInputStream == null){
            jarHandler.loadJarFile(DefaultConfigGroup.INPUT.getFile(configuration), false);
        }else{
            jarHandler.loadJarFile(jarInputStream, false);
        }
        for (File file : DefaultConfigGroup.LIBRARIES.getObj(configuration)){
            try{
                jarHandler.loadJarFile(file, true);
            }catch (Exception e){
                logger.warn("Something went wrong while loading a library");
            }
        }
        logger.info("Successfully created and loaded jar handler");

        logger.info("Creating mutators...");
        Set<Mutator> mutatorsSet = injector.getInstance(Key.get(new TypeLiteral<>() {}));
        List<Mutator> mutators = mutatorsSet.stream().sorted(
                Comparator.comparingInt(mutator -> {
                    if (mutator.getPriority() == Integer.MIN_VALUE) // Overflows back to min_value
                        return Integer.MAX_VALUE;
                    return -mutator.getPriority();
                })
        ).collect(Collectors.toList());
        logger.info("Created mutator...");

        // We need to occasionally check for interrupts in case we need to cancel the job
        checkForInterrupt();

        // As documented in the Mutator class, setup is called first
        // for every mutator
        for (Mutator mutator : mutators){
            if (mutator.isDisabled())
                continue;
            logger.info("Calling setup for \"" +
                    mutator.getName() + "\" mutator...");
            mutator.setup();
            logger.info("Finished calling setup for \"" +
                    mutator.getName() + "\" mutator");
        }

        // In case user cancelled job
        checkForInterrupt();

        // Again, as documented, multiple passes of transformation methods in a specific order.
        // It's a bit of a mess, but it's also more of a length than a complexity thing
        for (Mutator mutator : mutators){
            if (mutator.isDisabled())
                continue;
            safelyLoopOverEntries(jarHandler, entry -> {
                if (!(entry instanceof ClassEntry)
                        || isEntryBlacklistedForMutator(mutator.getClass(), entry))
                    return;
                logger.debug("Preforming first pass for class entry \"" +
                        entry.getPath() + "\"...");
                mutator.firstPassClassTransform((ClassEntry) entry);
                logger.debug("Finished first pass for class entry \"" +
                        entry.getPath() + "\"...");
            });
            checkForInterrupt();
            safelyLoopOverEntries(jarHandler, entry -> {
                if (!(entry instanceof ResourceEntry)
                        || isEntryBlacklistedForMutator(mutator.getClass(), entry))
                    return;
                logger.debug("Preforming first pass for resource entry \"" +
                        entry.getPath() + "\"...");
                mutator.firstPassResourceTransform((ResourceEntry) entry);
                logger.debug("Finished first pass for resource entry \"" +
                        entry.getPath() + "\"...");
            });
            checkForInterrupt();
            mutator.intermission();
            checkForInterrupt();
            safelyLoopOverEntries(jarHandler, entry -> {
                if (!(entry instanceof ClassEntry)
                        || isEntryBlacklistedForMutator(mutator.getClass(), entry))
                    return;
                logger.debug("Preforming second pass for class entry \"" +
                        entry.getPath() + "\"...");
                mutator.secondPassClassTransform((ClassEntry) entry);
                logger.debug("Finished second pass for class entry \"" +
                        entry.getPath() + "\"...");
            });
            checkForInterrupt();
            safelyLoopOverEntries(jarHandler, entry -> {
                if (!(entry instanceof ResourceEntry)
                        || isEntryBlacklistedForMutator(mutator.getClass(), entry))
                    return;
                logger.debug("Preforming second pass for resource entry \"" +
                        entry.getPath() + "\"...");
                mutator.secondPassResourceTransform((ResourceEntry) entry);
                logger.debug("Finished second pass for resource entry \"" +
                        entry.getPath() + "\"...");
            });
        }
        checkForInterrupt();

        // As documented in the Mutator class, cleanup is called last on every mutator
        for (Mutator mutator : mutators){
            if (mutator.isDisabled())
                continue;
            logger.info("Calling cleanup for \"" +
                    mutator.getName() + "\" mutator...");
            mutator.cleanup();
            logger.info("Finished calling cleanup for \"" +
                    mutator.getName() + "\" mutator");
        }

        jarHandler.writeJarFile(DefaultConfigGroup.OUTPUT.getFile(configuration),
                DefaultConfigGroup.COMPUTE_FRAMES.getBool(configuration),
                DefaultConfigGroup.COMPUTE_MAXES.getBool(configuration),
                DefaultConfigGroup.INCLUDE_LIBS_IN_OUTPUT.getBool(configuration));
//        jarHandler = null; Black and whitelist tests have issues when the jar is cleaned up by default

        logger.info("Finished obfuscation job");
    }

    /**
     * Checks if an entry is blacklisted for a specific mutator
     * @param mutatorClass The mutator class to check when using black and white list
     * @param entry The entry to compare the matchers to
     * @return true if the object shouldn't be run under the mutator, otherwise false
     */
    private boolean isEntryBlacklistedForMutator(Class<? extends Mutator> mutatorClass,
                                                 FileEntry<?> entry){
        String key = mutatorClass.getName();
        RegexMapFilter blacklist = new RegexMapFilter(
                DefaultConfigGroup.BLACKLIST.getObj(configuration).getMappings()
        );
        RegexMapFilter whitelist = new RegexMapFilter(
                DefaultConfigGroup.WHITELIST.getObj(configuration).getMappings()
        );
        boolean isBlacklisted = blacklist.matches(key, entry.getPath())
                || blacklist.matches(null /* global blacklist */, entry.getPath());
        boolean isWhitelisted = whitelist.matches(key, entry.getPath())
                || whitelist.matches(null /* global whitelist */, entry.getPath());
        return isBlacklisted && !isWhitelisted;
    }

    /**
     * Loops over entries of a JarHandler in such a way
     * where it is safe for any mutators to move or remove
     * nodes from the entry linked list
     * @param handler The handler to get the entries from
     * @param consumer Callback consumer. Will be fed every entry except dummies
     */
    private void safelyLoopOverEntries(JarHandler handler, Consumer<FileEntry<?>> consumer){
        FileEntry<?> currentEntry = handler.getFirstEntry();
        while (currentEntry != null){
            // We get the next entry before calling any consumers as they may remove or change
            // where the current entry is on the list. If this node is moved to the end then it would
            // end the job early because it assumes that it is the last node. Keeping track of the next
            // node here will prevent that.
            FileEntry<?> nextEntry = currentEntry.getNextNode();

            // We now callback to the desired consumer
            boolean shouldProcess = currentEntry.isShouldMutate()
                    && !(currentEntry instanceof DummyEntry);
            if (shouldProcess)
                consumer.accept(currentEntry);

            // This is to ensure newly added classes aren't dropped by our loop. To explain:
            // When a new class is created by our tool, it is added to the end of the linked list.
            // While we are able to remove nodes from the linked list, we are not able to insert them
            // anywhere but the end. Our next node is logged before the add operation is done by the mutator.
            // If a mutator adds a new node on the last entry then it would drop the entry and not pass it to
            // our mutators. This check ensures that any new nodes be processed.
            if (nextEntry == null && currentEntry.getNextNode() != null)
                nextEntry = currentEntry.getNextNode();

            // Only now do we change the current entry variable
            currentEntry = nextEntry;
        }
    }

    private void checkForInterrupt() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException("Obfuscation job was interrupted.");
        }
    }

    public boolean hasStarted() {
        return hasStarted;
    }

    public boolean hasFailed() {
        return failed;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    protected Configuration getConfig() {
        return configuration;
    }

    public JarHandler getJarHandler() {
        return jarHandler;
    }

    public String getThreadId() {
        return threadId;
    }
}
