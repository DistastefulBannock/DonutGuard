package me.bannock.donutguard.obf.mutator.impl;

import com.google.inject.Inject;
import me.bannock.donutguard.obf.asm.entry.impl.ClassEntry;
import me.bannock.donutguard.obf.config.Configuration;
import me.bannock.donutguard.obf.mutator.Mutator;
import me.bannock.donutguard.obf.mutator.cfg.NopSpamCfgGroup;
import me.bannock.donutguard.utils.AsmUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;

public class NopSpammerMutator extends Mutator {

    private final Logger logger = LogManager.getLogger();
    private final Configuration config;
    private int nopsCreated;

    @Inject
    public NopSpammerMutator(Configuration config){
        super("NOP Spammer", NopSpamCfgGroup.NOP_SPAM_ENABLED.getBool(config));
        this.config = config;
    }

    @Override
    public void setup() {
        nopsCreated = 0;
    }

    @Override
    public void firstPassClassTransform(ClassEntry entry) {
        AsmUtils.loopOverMethods(entry, methodNode -> methodNode.instructions.forEach(insn -> {
            for (int i = 0; i < NopSpamCfgGroup.NOP_SPAM_COUNT.getInt(config); i++){
                methodNode.instructions.insertBefore(insn, new InsnNode(Opcodes.NOP));
                nopsCreated++;
            }
        }));
    }

    @Override
    public void cleanup() {
        logger.info(String.format("Created %s NOPs", this.nopsCreated));
        nopsCreated = 0;
    }
}
