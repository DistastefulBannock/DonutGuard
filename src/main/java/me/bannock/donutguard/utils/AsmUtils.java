package me.bannock.donutguard.utils;

import me.bannock.donutguard.obf.asm.entry.impl.ClassEntry;
import me.bannock.donutguard.obf.config.Configuration;
import me.bannock.donutguard.obf.config.DefaultConfigGroup;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AsmUtils {

    /**
     * Iterates over every method in a ClassNode
     * @param node The ClassNode to fetch methods from
     * @param consumer The consumer to call back to
     */
    public static void loopOverMethods(ClassNode node, Consumer<MethodNode> consumer){
        node.methods.forEach(consumer);
    }

    /**
     * Iterates over every method in a ClassEntry's ClassNode
     * @param entry The entry to pull the ClassNode from
     * @param consumer The consumer to call back to
     */
    public static void loopOverMethods(ClassEntry entry, Consumer<MethodNode> consumer){
        loopOverMethods(entry.getContent(), consumer);
    }

    /**
     * Loops over every insn in every method in a given class
     * @param node The node to serach for methods and insns
     * @param biconsumer The callback biconsumer
     */
    public static void loopOverAllInsn(ClassNode node, BiConsumer<MethodNode, AbstractInsnNode> biconsumer){
        loopOverMethods(node, methodNode ->
                methodNode.instructions.forEach(insn -> biconsumer.accept(methodNode, insn)));
    }

    /**
     * Loops over every insn in every method in a given class
     * @param entry The entry to serach for methods and insns
     * @param biconsumer The callback biconsumer
     */
    public static void loopOverAllInsn(ClassEntry entry, BiConsumer<MethodNode, AbstractInsnNode> biconsumer){
        loopOverAllInsn(entry.getContent(), biconsumer);
    }

    /**
     * Generates a method name that is guaranteed to be unique to the class
     * @param node The class node to check
     * @param config The config to grab the dictionaries from
     * @return A unique method name
     */
    public static String getUniqueMethodName(ClassNode node, Configuration config){
        // It's an array because java cries when you use lambdas with non-final local vars
        String[] name = {DefaultConfigGroup.METHOD_DICT.get(config).uniqueMethod()};
        while (node.methods.stream()
                .anyMatch(methodNode -> methodNode.name.equals(name[0]))){
            name[0] = DefaultConfigGroup.METHOD_DICT.get(config).uniqueMethod();
        }
        return name[0];
    }

    /**
     * Generates a method name that is guaranteed to be unique to the class
     * @param entry The class entry to check
     * @param config The config to grab the dictionaries from
     * @return A unique method name
     */
    public static String getUniqueMethodName(ClassEntry entry, Configuration config){
        return getUniqueMethodName(entry.getContent(), config);
    }

    /**
     * Generates a field name that is guaranteed to be unique to the class
     * @param node The class node to check
     * @param config The config to grab the dictionaries from
     * @return A unique field name
     */
    public static String getUniqueFieldName(ClassNode node, Configuration config){
        // It's an array because java cries when you use lambdas with non-final local vars
        String[] name = {DefaultConfigGroup.FIELD_DICT.get(config).uniqueField()};
        while (node.fields.stream()
                .anyMatch(fieldNode -> fieldNode.name.equals(name[0]))){
            name[0] = DefaultConfigGroup.FIELD_DICT.get(config).uniqueField();
        }
        return name[0];
    }

    /**
     * Generates a field name that is guaranteed to be unique to the class
     * @param entry The class entry to check
     * @param config The config to grab the dictionaries from
     * @return A unique field name
     */
    public static String getUniqueFielddName(ClassEntry entry, Configuration config){
        return getUniqueFieldName(entry.getContent(), config);
    }

}
