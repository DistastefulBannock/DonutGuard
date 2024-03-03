package me.bannock.donutguard.obf.mutator.impl.calls;

import me.bannock.donutguard.obf.config.ConfigKey;
import me.bannock.donutguard.obf.config.ConfigurationGroup;

public class IndyConfigGroup extends ConfigurationGroup {

    public static final ConfigKey<Boolean> REPLACE_WITH_INDY_ENABLED =
            new ConfigKey<>("Replace with invoke dynamic enabled", false);

}
