package me.bannock.donutguard.obf.mutator.impl.calls;

import com.google.inject.Inject;
import me.bannock.donutguard.obf.asm.entry.impl.ClassEntry;
import me.bannock.donutguard.obf.config.Configuration;
import me.bannock.donutguard.obf.mutator.Mutator;
import me.bannock.donutguard.utils.AsmUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashSet;

public class ReplaceWithInDyMutator extends Mutator {

    private final Logger logger = LogManager.getLogger();
    private final Configuration config;
    private final HashSet<MethodNode> proxies = new HashSet<>();

    @Inject
    public ReplaceWithInDyMutator(Configuration config) {
        super("ReplaceWithInDyMutator",
                IndyConfigGroup.REPLACE_WITH_INDY_ENABLED.get(config));
        this.config = config;
    }

    @Override
    public void setup() {
        proxies.clear();
    }

    @Override
    public void cleanup() {
        logger.info(String.format("Created %s proxy methods", proxies.size()));
        proxies.clear();
    }

    @Override
    public void firstPassClassTransform(ClassEntry entry) {
        AsmUtils.loopOverAllInsn(entry, ((methodNode, abstractInsnNode) -> {
            if (proxies.contains(methodNode))
                return;
            if (abstractInsnNode instanceof MethodInsnNode){
                MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
                int indyOpcode = 0;
                if (methodInsnNode.getOpcode() != Opcodes.INVOKESTATIC)
                    return;
                switch (methodInsnNode.getOpcode()){
                    case Opcodes.INVOKEVIRTUAL:{
                        indyOpcode = Opcodes.H_INVOKEVIRTUAL;
                    }break;
                    case Opcodes.INVOKESPECIAL:{
                        indyOpcode = Opcodes.H_INVOKESPECIAL;
                    }break;
                    case Opcodes.INVOKESTATIC:{
                        indyOpcode = Opcodes.H_INVOKESTATIC;
                    }break;
                    case Opcodes.INVOKEINTERFACE:{
                        indyOpcode = Opcodes.H_INVOKEINTERFACE;
                    }break;
                }

                MethodNode proxyMethod = new MethodNode(
                        Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                        AsmUtils.getUniqueMethodName(entry, config),
                        "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;" +
                                "Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;",
                        null, new String[]{"java/lang/ReflectiveOperationException"});
                proxies.add(proxyMethod);

                // Populate method proxy
                entry.getContent().methods.add(proxyMethod);
                proxyMethod.instructions = new InsnList();
                populateProxyMethod(proxyMethod);

                // Replace call
                methodNode.instructions.insertBefore(methodInsnNode,
                        new InvokeDynamicInsnNode(
                                methodInsnNode.owner  + "." + methodInsnNode.name,
                                methodInsnNode.desc, new Handle(Opcodes.H_INVOKESTATIC,
                                entry.getContent().name, proxyMethod.name,
                                proxyMethod.desc, false)));
                if ((methodInsnNode.owner + "." + methodInsnNode.name + " " + methodNode.desc)
                        .contains("dev/sim0n/evaluator/util/Log"))
                    System.out.println();
                methodNode.instructions.remove(abstractInsnNode);

            }
        }));
    }

    private void populateProxyMethod(MethodNode proxyNode){
        final int LOOKUP = 0;
        final int NAME = 1;
        final int METHOD_TYPE = 2;
        final int DECLARING_CLASS = 3;

        proxyNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, LOOKUP)); // MethodHandles.Lookup
        proxyNode.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                "java/lang/invoke/MethodHandles$Lookup", "lookupClass",
                "()Ljava/lang/Class;"));
        proxyNode.instructions.add(new VarInsnNode(Opcodes.ASTORE, DECLARING_CLASS));

        proxyNode.instructions.add(new TypeInsnNode(Opcodes.NEW,
                "java/lang/invoke/ConstantCallSite")); // A
        proxyNode.instructions.add(new InsnNode(Opcodes.DUP)); // A, A
        proxyNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, LOOKUP)); // A, A, A
        proxyNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, DECLARING_CLASS)); // A, A, A, A
        proxyNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, NAME)); // A, A, A, A, A
        proxyNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, METHOD_TYPE)); // A, A, A, A, A, A
        proxyNode.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                "java/lang/invoke/MethodHandles$Lookup", "findStatic",
                "(Ljava/lang/Class;Ljava/lang/String;" +
                        "Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;")); // A, A, A
        proxyNode.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,
                "java/lang/invoke/ConstantCallSite", "<init>",
                "(Ljava/lang/invoke/MethodHandle;)V")); // A
        proxyNode.instructions.add(new InsnNode(Opcodes.ARETURN));
    }

    public static CallSite testBootstrapMethod(MethodHandles.Lookup lookup,
                                               String name, MethodType type){
        // Retrieve the class of the invoked object from the lookup object
        Class<?> declaringClass = lookup.lookupClass();

        // Implement your logic to find the appropriate method based on name, type, and class
        // Here, we simply search for a matching method in the same class
        try {
            return new ConstantCallSite(lookup.findStatic(declaringClass, name, type));
        } catch (ReflectiveOperationException e) {
            // Handle potential exceptions (e.g., method not found)
            throw new RuntimeException("Failed to find method: " + e.getMessage());
        }
    }

}
