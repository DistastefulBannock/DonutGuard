package me.bannock.donutguard.obf.mutator.cfg;

import me.bannock.donutguard.obf.config.ConfigurationGroup;
import me.bannock.donutguard.obf.config.impl.ConfigKeyBoolean;
import me.bannock.donutguard.obf.config.impl.ConfigKeyInteger;

public class NopSpamCfgGroup extends ConfigurationGroup {

    public static final ConfigKeyBoolean NOP_SPAM_ENABLED =
            new ConfigKeyBoolean("NOP spammer enabled", false);
    public static final ConfigKeyInteger NOP_SPAM_COUNT =
            new ConfigKeyInteger("NOP spam count", 2);

}
