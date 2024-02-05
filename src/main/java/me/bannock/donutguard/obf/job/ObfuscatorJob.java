package me.bannock.donutguard.obf.job;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.obf.asm.entry.FileEntry;
import me.bannock.donutguard.obf.asm.JarHandler;
import me.bannock.donutguard.obf.asm.entry.impl.ClassEntry;
import me.bannock.donutguard.obf.asm.entry.impl.DummyEntry;
import me.bannock.donutguard.obf.asm.entry.impl.ResourceEntry;
import me.bannock.donutguard.obf.filter.RegexListFilter;
import me.bannock.donutguard.obf.mutator.Mutator;
import me.bannock.donutguard.obf.mutator.impl.NopSpammerMutator;
import me.bannock.donutguard.obf.mutator.impl.TestMutator;
import me.bannock.donutguard.obf.mutator.impl.string.LineNumberStringLiteralMutator;
import me.bannock.donutguard.utils.UiUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.function.Consumer;

public class ObfuscatorJob implements Runnable {

    private final Logger logger = LogManager.getLogger();
    private String threadId = "Obfuscator Job " + System.currentTimeMillis() + "." + System.nanoTime(); // This will be replaced by the obfuscator

    private final ConfigDTO configDTO;

    private boolean hasStarted = false, failed = false;

    private JarHandler jarHandler;

    @Inject
    public ObfuscatorJob(ConfigDTO configDTO, JarHandler jarHandler){
        this.configDTO = SerializationUtils.clone(configDTO);
        this.jarHandler = jarHandler;
    }

    /**
     * Populates a hashset of mutators with all the mutators that the obfuscator offers
     * @param mutators The set to populate
     * @param injector The injector used to create mutator instances
     */
    private void createMutators(HashSet<Mutator> mutators, Injector injector){
        mutators.add(injector.getInstance(NopSpammerMutator.class));
        mutators.add(injector.getInstance(LineNumberStringLiteralMutator.class));
        mutators.add(injector.getInstance(TestMutator.class));
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
                UiUtils.showErrorMessage("Obfuscator Error", "An error occurred while running the obfuscator." +
                        "\nCheck the logs for more information.");
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
        Injector injector = Guice.createInjector(new JobModule(this));
        logger.info("Successfully created job injector");
        logger.info("Creating and loading jar handler...");
        this.jarHandler = injector.getInstance(JarHandler.class);
        jarHandler.loadJarFile(getConfigDTO().input, false);
        for (File file : configDTO.libraries){
            try{
                jarHandler.loadJarFile(file, true);
            }catch (Exception e){
                logger.warn("Something went wrong while loading a library");
            }
        }
        logger.info("Successfully created and loaded jar handler");

        // We need to occasionally check for interrupts in case we need to cancel the job
        checkForInterrupt();

        // Create mutators
        logger.info("Creating mutators...");
        HashSet<Mutator> mutators = new LinkedHashSet<>();
        createMutators(mutators, injector);
        logger.info("Successfully created mutators");

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
                if (!(entry instanceof ClassEntry))
                    return;
                logger.debug("Preforming first pass for class entry \"" +
                        entry.getPath() + "\"...");
                mutator.firstPassClassTransform((ClassEntry) entry);
                logger.debug("Finished first pass for class entry \"" +
                        entry.getPath() + "\"...");
            });
            checkForInterrupt();
            safelyLoopOverEntries(jarHandler, entry -> {
                if (!(entry instanceof ResourceEntry))
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
                if (!(entry instanceof ClassEntry))
                    return;
                logger.debug("Preforming second pass for class entry \"" +
                        entry.getPath() + "\"...");
                mutator.secondPassClassTransform((ClassEntry) entry);
                logger.debug("Finished second pass for class entry \"" +
                        entry.getPath() + "\"...");
            });
            checkForInterrupt();
            safelyLoopOverEntries(jarHandler, entry -> {
                if (!(entry instanceof ResourceEntry))
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

        jarHandler.writeJarFile(configDTO.output, configDTO.computeFrames,
                configDTO.computeMaxes, configDTO.includeLibsInOutput);

        logger.info("Finished obfuscation job");
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
        RegexListFilter blacklist = new RegexListFilter(configDTO.blacklist);
        RegexListFilter whitelist = new RegexListFilter(configDTO.whitelist);
        while (currentEntry != null){
            // We get the next entry before calling any consumers as they may remove or change
            // where the current entry is on the list. If this node is moved to the end then it would
            // end the job early because it assumes that it is the last node. Keeping track of the next
            // node here will prevent that.
            FileEntry<?> nextEntry = currentEntry.getNextNode();

            // We now callback to the desired consumer
            boolean shouldProcess = currentEntry.isShouldMutate()
                    && !(currentEntry instanceof DummyEntry)
                    && (!blacklist.matches(currentEntry.getPath())
                    || whitelist.matches(currentEntry.getPath()));
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

    protected ConfigDTO getConfigDTO() {
        return configDTO;
    }

    public JarHandler getJarHandler() {
        return jarHandler;
    }

    public String getThreadId() {
        return threadId;
    }
}
