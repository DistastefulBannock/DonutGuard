package me.bannock.donutguard.obf.mutator.impl;

import com.google.inject.Inject;
import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.obf.asm.impl.ClassEntry;
import me.bannock.donutguard.obf.mutator.Mutator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;

public class NopSpammerMutator extends Mutator {

    private final Logger logger = LogManager.getLogger();
    private final ConfigDTO config;
    @Inject
    public NopSpammerMutator(ConfigDTO configDTO){
        super("NOP Spammer", configDTO.nopSpammerEnabled);
        this.config = configDTO;
    }

    @Override
    public void firstPassClassTransform(ClassEntry entry) {
        loopOverMethods(entry, methodNode -> methodNode.instructions.forEach(insn -> {
            for (int i = 0; i < config.nopsPerInstruction; i++){
                methodNode.instructions.insertBefore(insn, new InsnNode(Opcodes.NOP));
            }
        }));
    }

}
