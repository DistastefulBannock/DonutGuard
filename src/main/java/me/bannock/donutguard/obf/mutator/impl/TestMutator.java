package me.bannock.donutguard.obf.mutator.impl;

import com.google.inject.Inject;
import me.bannock.donutguard.obf.asm.JarHandler;
import me.bannock.donutguard.obf.asm.entry.impl.ClassEntry;
import me.bannock.donutguard.obf.config.Configuration;
import me.bannock.donutguard.obf.config.DefaultConfigGroup;
import me.bannock.donutguard.obf.mutator.Mutator;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

public class TestMutator extends Mutator {

    private final Configuration config;
    private final JarHandler jarHandler;

    @Inject
    public TestMutator(Configuration config, JarHandler jarHandler) {
        super("Testing Mutator", DefaultConfigGroup.DEV_TEST_MUTATOR_ENABLED.get(config));
        this.config = config;
        this.jarHandler = jarHandler;
    }

    @Override
    public void firstPassClassTransform(ClassEntry entry) {
        // int version, int access, String name, String signature, String superName, String[] interfaces
        ClassNode newNode = new ClassNode();
        newNode.access = Opcodes.ACC_PUBLIC;
        newNode.name = DefaultConfigGroup.PACKAGE_DICT.get(config).uniquePackage() + "/" +
                DefaultConfigGroup.CLASS_DICT.get(config).uniqueClass();
        newNode.version = 52;
        ClassEntry clazz = new ClassEntry(newNode.name + ".class", false, newNode);
        jarHandler.getFirstEntry().addNodeToEnd(clazz);
    }

}
