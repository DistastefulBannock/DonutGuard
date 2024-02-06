package me.bannock.donutguard.obf.filter;

import com.google.inject.Inject;
import me.bannock.donutguard.obf.asm.entry.impl.ClassEntry;
import me.bannock.donutguard.obf.asm.entry.impl.ResourceEntry;
import me.bannock.donutguard.obf.config.Configuration;
import me.bannock.donutguard.obf.config.DefaultConfigGroup;
import me.bannock.donutguard.obf.mutator.Mutator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.junit.jupiter.api.Assertions.*;

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
    public void firstPassClassTransform(ClassEntry entry) {
        RegexMapFilter blacklist = new RegexMapFilter(
                DefaultConfigGroup.BLACKLIST.get(config)
        );
        RegexMapFilter whitelist = new RegexMapFilter(
                DefaultConfigGroup.WHITELIST.get(config)
        );
        assertTrue(!blacklist.matches(getClass().getName(), entry.getPath())
                || whitelist.matches(getClass().getName(), entry.getPath()));
    }

    @Override
    public void firstPassResourceTransform(ResourceEntry entry) {
        RegexMapFilter blacklist = new RegexMapFilter(
                DefaultConfigGroup.BLACKLIST.get(config)
        );
        RegexMapFilter whitelist = new RegexMapFilter(
                DefaultConfigGroup.WHITELIST.get(config)
        );
        assertTrue(!blacklist.matches(getClass().getName(), entry.getPath())
                || whitelist.matches(getClass().getName(), entry.getPath()));
    }

    @Override
    public void cleanup() {
        DefaultConfigGroup.NOP_SPAM_ENABLED.set(config, true);
    }
}