package me.bannock.donutguard.obf.plugin;

import me.bannock.donutguard.obf.mutator.Mutator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThirdPartyMutatorTest extends Mutator {
        private final Logger logger = LogManager.getLogger();
        public ThirdPartyMutatorTest() {
            super("Third Party Test Mutator", true);
        }

        @Override
        public void setup() {
            logger.info("This has run, which means the mutator was added correctly.");
        }
    }