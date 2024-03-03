package me.bannock.donutguard.obf.mutator.cfg;

import me.bannock.donutguard.obf.config.ConfigKey;
import me.bannock.donutguard.obf.config.ConfigurationGroup;

public class NopSpamCfgGroup extends ConfigurationGroup {

    public static final ConfigKey<Boolean> NOP_SPAM_ENABLED =
            new ConfigKey<>("NOP spammer enabled", false);
    public static final ConfigKey<Integer> NOP_SPAM_COUNT =
            new ConfigKey<>("NOP spam count", 2);

}
