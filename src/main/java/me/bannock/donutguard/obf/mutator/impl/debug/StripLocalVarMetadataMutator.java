package me.bannock.donutguard.obf.mutator.impl.debug;

import com.google.inject.Inject;
import me.bannock.donutguard.obf.asm.entry.impl.ClassEntry;
import me.bannock.donutguard.obf.config.Configuration;
import me.bannock.donutguard.obf.config.DefaultConfigGroup;
import me.bannock.donutguard.obf.mutator.Mutator;
import me.bannock.donutguard.utils.AsmUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StripLocalVarMetadataMutator extends Mutator {

    private final Logger logger = LogManager.getLogger();
    private int localsRemoved = 0;

    @Inject
    public StripLocalVarMetadataMutator(Configuration config) {
        super("Strip local var metadata",
                DefaultConfigGroup.STRIP_LOCAL_VAR_METADATA.get(config));
    }

    @Override
    public void setup() {
        this.localsRemoved = 0;
    }

    @Override
    public void firstPassClassTransform(ClassEntry entry) {
        AsmUtils.loopOverMethods(entry, methodNode -> methodNode.localVariables.clear());
    }

    @Override
    public void cleanup() {
        logger.info(String.format("Removed metadata for %s local variables.", this.localsRemoved));
    }

}
