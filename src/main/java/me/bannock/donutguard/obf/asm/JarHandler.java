package me.bannock.donutguard.obf.asm;

import com.google.inject.Inject;
import me.bannock.donutguard.obf.asm.impl.ClassEntry;
import me.bannock.donutguard.obf.asm.impl.DummyEntry;
import me.bannock.donutguard.obf.asm.impl.ResourceEntry;
import me.bannock.donutguard.utils.IoUtils;
import me.bannock.donutguard.utils.ResourceUtils;
import me.bannock.donutguard.utils.UiUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;

public class JarHandler {

    private final Logger logger = LogManager.getLogger();

    // We use a dummy entry as the first entry because some mutators may
    // swap or move the first entry on the linked list. This is an easy way
    // to ensure that we always have the first entry in the linked list
    // since a dummy will never be given to a mutator
    private final DummyEntry firstEntry;

    @Inject
    public JarHandler() {
        firstEntry = new DummyEntry();
    }

    /**
     * Loads a jar file as well as its entries into this jar handler.
     * Will not clear any existing entries.
     * @param file The jar file to load
     * @param shouldMutate Whether the mutators should mutate the classes in this jar
     * @throws RuntimeException If an error occurs while loading the jar file
     */
    public void loadJarFile(File file, boolean shouldMutate) throws RuntimeException{
        logger.info("Loading jar file...");

        try (JarFile jarFile = new JarFile(file)) {
            jarFile.entries().asIterator()
                    .forEachRemaining(getEntryConsumer(jarFile, shouldMutate));
            logger.info("Successfully opened jar file \"" +
                    jarFile.getName() + " " + jarFile.getVersion() + "\"");
        } catch (IOException e) {
            logger.error("Failed to read jar file", e);
            throw new RuntimeException("Failed to read jar file", e);
        }

    }

    /**
     * Writes and jar file as well as its entries to a file
     * @param file The file to write the jar to
     * @param computeFrames Whether the class writer should compute frames
     * @param computeMaxes Whether the class writer should compute maxes
     * @param includeNonMutated Whether non-mutated classes should be included in the built jar
     * @throws RuntimeException If something went wrong while writing the file
     * @throws InterruptedException If the thread is interrupted while writing
     */
    public void writeJarFile(File file, boolean computeFrames, boolean computeMaxes,
                             boolean includeNonMutated) throws RuntimeException, InterruptedException{
        logger.info("Writing jar file...");
        final String classWatermark = ResourceUtils.readString("obf/classWatermark.txt");
        final String jarWatermark = ResourceUtils.readString("obf/jarWatermark.txt");

        boolean hadDuplicateEntries = false;
        try(FileOutputStream fos = new FileOutputStream(file);
            JarOutputStream jos = new JarOutputStream(fos)){

            logger.info("Writing entries...");
            FileEntry<?> lastEntry = firstEntry;
            while ((lastEntry = lastEntry.getNextNode()) != null){
                if (Thread.interrupted())
                    throw new InterruptedException("Obfuscation job was interrupted");
                if (lastEntry instanceof DummyEntry)
                    continue; // Ignored
                logger.info("Writing " + lastEntry.getPath() + "...");

                ZipEntry zipEntry = new ZipEntry(lastEntry.getPath());
                try{
                    jos.putNextEntry(zipEntry);
                }catch (ZipException e){
                    logger.warn("Error while putting next entry \"" +
                            lastEntry.getPath() + "\" (duplicate?)", e);
                    hadDuplicateEntries = true;
                    continue;
                }

                if (lastEntry instanceof ResourceEntry){
                    ResourceEntry entry = (ResourceEntry) lastEntry;
                    if (entry.isShouldMutate() || includeNonMutated)
                        jos.write(entry.getContent());
                }
                else if (lastEntry instanceof ClassEntry){
                    ClassEntry entry = (ClassEntry) lastEntry;
                    if (entry.isShouldMutate() || includeNonMutated){
                        ClassWriter classWriter = getClassWriter(computeFrames, computeMaxes);
                        entry.getContent().accept(classWriter);
                        // This adds a string to the constant pool, effectively watermarking the class
                        classWriter.newUTF8(classWatermark);
                        jos.write(classWriter.toByteArray());
                    }
                }
                jos.flush();
                logger.info("Finished writing " + lastEntry.getPath());
            }
            logger.info("Finished writing entries");

            logger.info("Adding watermark...");
            jos.setComment(jarWatermark);
            logger.info("Added watermark");

            if (hadDuplicateEntries){
                UiUtils.showErrorMessage("Had duplicate entries",
                        "Duplicate entries were found while writing the jar." +
                                "\nThis may cause unexpected issues. Affected classes " +
                                "can be found in the logs");
            }

        } catch (IOException e){
            logger.error("Something went wrong while writing jar file", e);
            throw new RuntimeException("Something went wrong while writing jar file", e);
        }
        logger.info("Successfully wrote jar file");
    }

    /**
     * Creates a new class writer
     * @param computeFrames Whether the class writer should compute frames
     * @param computeMaxes Whether the class writer should compute maxes
     * @return A new classwriter
     */
    private ClassWriter getClassWriter(boolean computeFrames, boolean computeMaxes){
        int flags = 0;
        if (computeFrames)
            flags |= ClassWriter.COMPUTE_FRAMES;
        if (computeMaxes)
            flags |= ClassWriter.COMPUTE_MAXS;
        return new ClassWriter(flags) {
            @Override
            protected String getCommonSuperClass(String type1, String type2) {
                // We need to override this because the class writer class is garbage
                try {
                    return super.getCommonSuperClass(type1, type2);
                } catch (Exception e) {
                    return "java/lang/Object";
                }
            }
        };
    }

    private Consumer<? super JarEntry> getEntryConsumer(JarFile jarFile,
                                                        boolean shouldMutateEntries){
        return entry -> {
            if (entry == null)
                return;

            logger.info("Loading entry \"" + entry.getName() + "\"...");

            String name = entry.getName();
            byte[] bytes;
            try (InputStream in = jarFile.getInputStream(entry)){
                bytes = IoUtils.readBytesFromInputStream(in);
            }catch (IOException e){
                logger.error("Something went wrong while reading " +
                        "the entry's input stream.", e);
                return;
            }

            // The file entry class we use depends on whether the entry
            // is of a jar file
            FileEntry<?> newEntry;
            if (name.toLowerCase().endsWith(".class")){
                newEntry = new ClassEntry(name, shouldMutateEntries, bytes);
            }else{
                newEntry = new ResourceEntry(name, true, bytes);
            }
            firstEntry.addNodeToEnd(newEntry);
            logger.info("Successfully loaded entry \"" + entry.getName() + "\"");
        };
    }

    public FileEntry<?> getFirstEntry() {
        return firstEntry;
    }
}
