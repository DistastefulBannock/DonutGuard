package me.bannock.donutguard.obf.mutator;

import com.google.inject.Inject;
import me.bannock.donutguard.obf.config.Configuration;
import me.bannock.donutguard.obf.mutator.cfg.NopSpamCfgGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThirdPartyMutatorTest extends Mutator {
    private final Logger logger = LogManager.getLogger();
    private final Configuration config;

    @Inject
    public ThirdPartyMutatorTest(Configuration config) {
        super("Third Party Test Mutator", true);
        this.config = config;
    }

    @Override
    public void setup() {
        logger.info("This has run, which means the mutator was added correctly.");
    }

    @Override
    public void cleanup() {
        NopSpamCfgGroup.NOP_SPAM_ENABLED.set(config, true);
    }

}