package me.bannock.donutguard.obf.asm.entry.impl;

import me.bannock.donutguard.obf.asm.entry.FileEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public class ClassEntry extends FileEntry<ClassNode> {

    private final Logger logger = LogManager.getLogger();
    private ClassReader classReader;
    private final List<BiConsumer<ClassNode, ClassWriter>> classWriterTasks;

    /**
     * @param path The path of the entry inside the jar
     * @param shouldMutate Whether the program should mutate this entry
     * @param libraryEntry Whether the class is part of a library
     * @param classBytes The bytes of the classs
     */
    public ClassEntry(String path, boolean shouldMutate, boolean libraryEntry, byte[] classBytes) {
        super(path, shouldMutate, libraryEntry, null);
        this.classWriterTasks = new ArrayList<>();

        logger.debug("Reading class bytes...");
        try{
            this.classReader = new ClassReader(classBytes);
            logger.debug("Creating ClassNode object from read bytes...");
            ClassNode node = new ClassNode();
            classReader.accept(node, 0);
            setContent(node);
            logger.debug("Successfully created ClassNode object");
        }catch (Exception e){
            logger.error("An error occurred while reading class bytes", e);
            throw new RuntimeException("An error occurred while reading class bytes", e);
        }
    }

    /**
     * @param path The path of the entry inside the jar
     * @param shouldMutate Whether the program should mutate this entry
     * @param classBytes The bytes of the classs
     */
    public ClassEntry(String path, boolean shouldMutate, byte[] classBytes) {
        this(path, shouldMutate, false, classBytes);
    }

    /**
     * @param path The path of the entry inside the jar
     * @param shouldMutate Whether the program should mutate this entry
     * @param node The ClassNode object
     */
    public ClassEntry(String path, boolean shouldMutate, ClassNode node) {
        super(path, shouldMutate, false, node);
        this.classWriterTasks = new ArrayList<>();
    }

    /**
     * Adds a classwriter task to the queue. All tasks are ran
     * as the classwriter compiles the class. These also differ
     * from the regular mutator methods in that all consumers,
     * even those from other mutators, are ran at once.
     * These are the absolute last thing ran before saving.
     * If you need to use the classwriter for something then
     * this is what you'd use.
     * Please also keep in mind that the passed-in ClassNode objects
     * have already been compiled by the ClassWriter. This means
     * that any changes done to them will not appear in the final jar.
     * @param consumer The consumer to run
     */
    public void addClassWriterTask(BiConsumer<ClassNode, ClassWriter> consumer){
        classWriterTasks.add(consumer);
    }

    /**
     * @return An unmodifiable list containing all the class writer tasks.
     */
    public List<BiConsumer<ClassNode, ClassWriter>> getClassWriterTasks() {
        return Collections.unmodifiableList(classWriterTasks);
    }

    public ClassReader getClassReader() {
        return classReader;
    }

    public void setClassReader(ClassReader classReader) {
        this.classReader = classReader;
    }
}
