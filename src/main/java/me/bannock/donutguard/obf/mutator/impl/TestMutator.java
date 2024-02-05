package me.bannock.donutguard.obf.mutator.impl;

import com.google.inject.Inject;
import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.obf.asm.JarHandler;
import me.bannock.donutguard.obf.asm.impl.ClassEntry;
import me.bannock.donutguard.obf.asm.impl.ResourceEntry;
import me.bannock.donutguard.obf.mutator.Mutator;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

public class TestMutator extends Mutator {

    private final ConfigDTO config;
    private final JarHandler jarHandler;

    @Inject
    public TestMutator(ConfigDTO config, JarHandler jarHandler) {
        super("Testing Mutator", config.devTestMutatorEnabled);
        this.config = config;
        this.jarHandler = jarHandler;
    }

    @Override
    public void firstPassClassTransform(ClassEntry entry) {
        // int version, int access, String name, String signature, String superName, String[] interfaces
        ClassNode newNode = new ClassNode();
        newNode.access = Opcodes.ACC_PUBLIC;
        newNode.name = config.packageDict.uniquePackage() + "/" + config.classDict.uniqueClass();
        newNode.version = 52;
        ClassEntry clazz = new ClassEntry(newNode.name + ".class", false, newNode);
        jarHandler.getFirstEntry().addNodeToEnd(clazz);
        jarHandler.getFirstEntry().addNodeToEnd(new ResourceEntry(config.packageDict.uniquePackage() + "/" + config.classDict.uniqueClass(),
                false, false, new byte[1]));
    }

}
