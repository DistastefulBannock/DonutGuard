package me.bannock.donutguard.obf.mutator.impl;

import com.google.inject.Inject;
import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.obf.asm.impl.ClassEntry;
import me.bannock.donutguard.obf.mutator.Mutator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NopSpammerMutator extends Mutator {

    private final Logger logger = LogManager.getLogger();

    @Inject
    public NopSpammerMutator(ConfigDTO configDTO){
        super("NOP Spammer", configDTO.nopSpammerEnabled);
    }

    @Override
    public void firstPassClassTransform(ClassEntry entry) {
        loopOverMethods(entry, methodNode -> methodNode.instructions.clear());
    }

}
