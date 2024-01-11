package me.bannock.donutguard.obf.asm.impl;

import me.bannock.donutguard.obf.asm.FileEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

public class ClassEntry extends FileEntry<ClassNode> {

    private final Logger logger = LogManager.getLogger();
    private ClassReader classReader;

    /**
     * @param path The path of the entry inside the jar
     * @param shouldMutate Whether the program should mutate this entry
     * @param classBytes The bytes of the classs
     */
    public ClassEntry(String path, boolean shouldMutate, byte[] classBytes) {
        super(path, false, shouldMutate, null);

        logger.info("Reading class bytes...");
        try{
            this.classReader = new ClassReader(classBytes);
            logger.info("Creating ClassNode object from read bytes...");
            ClassNode node = new ClassNode();
            classReader.accept(node, 0);
            setContent(node);
            logger.info("Successfully created ClassNode object");
        }catch (Exception e){
            logger.error("An error occurred while reading class bytes", e);
            throw new RuntimeException("An error occurred while reading class bytes", e);
        }
    }

    /**
     * @param path The path of the entry inside the jar
     * @param shouldMutate Whether the program should mutate this entry
     * @param node The ClassNode object
     */
    public ClassEntry(String path, boolean shouldMutate, ClassNode node) {
        super(path, false, shouldMutate, node);
    }

    public ClassReader getClassReader() {
        return classReader;
    }

    public void setClassReader(ClassReader classReader) {
        this.classReader = classReader;
    }
}
