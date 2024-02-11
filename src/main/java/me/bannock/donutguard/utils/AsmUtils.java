package me.bannock.donutguard.utils;

import me.bannock.donutguard.obf.asm.JarHandler;
import me.bannock.donutguard.obf.asm.entry.FileEntry;
import me.bannock.donutguard.obf.asm.entry.impl.ClassEntry;
import me.bannock.donutguard.obf.config.Configuration;
import me.bannock.donutguard.obf.config.DefaultConfigGroup;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        String[] name = new String[1];
        do{
            name[0] = DefaultConfigGroup.METHOD_DICT.get(config).uniqueMethod();
        }while (node.methods.stream()
                .anyMatch(methodNode -> methodNode.name.equals(name[0])));
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
        String[] name = new String[1];
        do{
            name[0] = DefaultConfigGroup.FIELD_DICT.get(config).uniqueField();
        }while (node.fields.stream()
                .anyMatch(fieldNode -> fieldNode.name.equals(name[0])));
        return name[0];
    }

    /**
     * Generates a field name that is guaranteed to be unique to the class
     * @param entry The class entry to check
     * @param config The config to grab the dictionaries from
     * @return A unique field name
     */
    public static String getUniqueFieldName(ClassEntry entry, Configuration config){
        return getUniqueFieldName(entry.getContent(), config);
    }

    /**
     * Creates a unique path for a new class
     * @param entry The entry to compare our new path to
     * @param config The config to grab the other class paths from
     * @return A unique class path
     */
    public static String getUniqueClassPath(FileEntry<?> entry, Configuration config){
        String classPath = null;
        do{
            StringBuilder classPathBuilder = new StringBuilder();
            for (int i = 0; i < DefaultConfigGroup.NESTED_PACKAGES.get(config); i++){
                classPathBuilder.append(DefaultConfigGroup.PACKAGE_DICT.get(config).uniquePackage());
                classPathBuilder.append("/");
            }
            classPathBuilder.append(DefaultConfigGroup.CLASS_DICT.get(config).uniqueClass());
            classPath = classPathBuilder.toString();
        }while (entry.containsPath(classPath + ".class")
                && entry.containsPath(classPath + ".class/"));
        return classPath;
    }

    /**
     * Creates a unique path for a new class
     * @param jarHandler The jar handler to compare our new path to
     * @param config The config to grab the other class paths from
     * @return A unique class path
     */
    public static String getUniqueClassPath(JarHandler jarHandler, Configuration config){
        return getUniqueClassPath(jarHandler.getFirstEntry(), config);
    }

    /**
     * Originally from BannockGuard
     * @param desc The desc of the variable you are checking
     * @return true if it is a primitive, otherwise false
     */
    public static boolean isPrimitive(String desc) {
        List<String> prims = Arrays.asList("J", "F", "D", "Z", "C", "I", "S", "B");
        return prims.contains(desc);
    }

    /**
     * Originally from BannockGuard
     * Finds the descs of all the args in a method
     * @param desc The desc of the method
     * @return An arraylist containing all the args descs
     */
    public static ArrayList<String> getMethodParamDescs(String desc) {
        ArrayList<String> argDescs = new ArrayList<>();
        boolean arraySoSkipNext = false, argIsCurrentlyClass = false;
        for (char c : desc.toCharArray()) {
            if (c == ')') {
                break;
            } else if (argIsCurrentlyClass) {
                if (c == ';') {
                    argIsCurrentlyClass = false;
                }
                argDescs.set(argDescs.size() - 1, argDescs.get(argDescs.size() - 1) + c);
            } else if (c == 'L') {
                argIsCurrentlyClass = true;
                if (arraySoSkipNext) {
                    arraySoSkipNext = false;
                    argDescs.set(argDescs.size() - 1, argDescs.get(argDescs.size() - 1) + c);
                    continue;
                }
                argDescs.add(String.valueOf(c));
            } else if (isPrimitive(String.valueOf(c))) {
                if (arraySoSkipNext) {
                    arraySoSkipNext = false;
                    argDescs.set(argDescs.size() - 1, argDescs.get(argDescs.size() - 1) + c);
                    continue;
                }
                argDescs.add(String.valueOf(c));
            } else if (c == '[') {
                arraySoSkipNext = true;
                argDescs.add(String.valueOf(c));
            }
        }
        return argDescs;
    }

    /**
     * Stores a variable to a local variable, sorts it out so it uses the correct instruction for prims. Expects the var to already be on the stack
     * @param insnList The insnlist to append to
     * @param localIndex The local index you are assigning
     * @param varDesc The desc of the var
     */
    public static void storeToLocal(InsnList insnList, int localIndex, String varDesc) {
        if (isPrimitive(varDesc)) {
            int opcode = Opcodes.ASTORE;
            switch (varDesc) {
                case "J":
                    opcode = Opcodes.LSTORE;
                    break;
                case "F":
                    opcode = Opcodes.FSTORE;
                    break;
                case "D":
                    opcode = Opcodes.DSTORE;
                    break;
                case "Z":
                case "C":
                case "I":
                case "S":
                case "B":
                    opcode = Opcodes.ISTORE;
                    break;
            }
            insnList.add(new VarInsnNode(opcode, localIndex));
        }else {
            insnList.add(new VarInsnNode(Opcodes.ASTORE, localIndex));
        }
    }

    /**
     * Loads a value from a local variable, sorts it out so it uses the correct instruction for prims
     * @param insnList The insnlist to append to
     * @param localIndex The local index you want to load
     * @param varDesc The desc of the var
     */
    public static void loadFromLocal(InsnList insnList, int localIndex, String varDesc) {
        if (isPrimitive(varDesc)) {
            int opcode = Opcodes.ALOAD;
            switch (varDesc) {
                case "J":
                    opcode = Opcodes.LLOAD;
                    break;
                case "F":
                    opcode = Opcodes.FLOAD;
                    break;
                case "D":
                    opcode = Opcodes.DLOAD;
                    break;
                case "Z":
                case "C":
                case "I":
                case "S":
                case "B":
                    opcode = Opcodes.ILOAD;
                    break;
            }
            insnList.add(new VarInsnNode(opcode, localIndex));
        }else {
            insnList.add(new VarInsnNode(Opcodes.ALOAD, localIndex));
        }
    }

    /**
     * Takes an int and returns the node needed to load it to the stack
     * @param value The int value
     * @return The node that would load this int to the stack
     */
    public static AbstractInsnNode toInsnNode(int value) {
        if (value == -1)
            return new InsnNode(Opcodes.ICONST_M1);

        else if (value < 0)
            return new LdcInsnNode(value);

        else if (value <= 5)
            return new InsnNode(value + 3);

        else if (value < Byte.MAX_VALUE)
            return new IntInsnNode(Opcodes.BIPUSH, value);

        else if (value < Short.MAX_VALUE)
            return new IntInsnNode(Opcodes.SIPUSH, value);

        return new LdcInsnNode(value);
    }

    /**
     * Creates a method invoke instruction to a specific method.
     * Keep in mind that invoke instructions INVOKEVIRTUAL,
     * INVOKEINTERFACE, INVOKESPECIAL require an object reference
     * to be loaded onto the stack. Instructions should be in the order
     * load called object -> load params -> invoke insn
     * @param calledMethod The method to call
     * @param calledOwner The owner class of the called method
     * @param isCallingSuperClass Whether we're calling a method in a super class
     * @return A method invoke instruction that calls the provided method
     */
    public static MethodInsnNode generateMethodCallFromNode(MethodNode calledMethod,
                                                            ClassNode calledOwner,
                                                            boolean isCallingSuperClass){
        int invokeOpcode = Opcodes.INVOKEVIRTUAL;
        boolean isInterface = Modifier.isInterface(calledMethod.access);
        if (isInterface)
            invokeOpcode = Opcodes.INVOKEINTERFACE;
        else if (Modifier.isStatic(calledMethod.access))
            invokeOpcode = Opcodes.INVOKESTATIC;
        else if (calledMethod.name.equals("<init>") || isCallingSuperClass)
            invokeOpcode = Opcodes.INVOKESPECIAL;
        return new MethodInsnNode(
                invokeOpcode,
                calledOwner.name,
                calledMethod.name,
                calledMethod.desc,
                isInterface
        );
    }

}
