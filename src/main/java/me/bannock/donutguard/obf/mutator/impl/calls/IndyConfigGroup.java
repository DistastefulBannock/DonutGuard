package me.bannock.donutguard.obf.mutator.impl.calls;

import me.bannock.donutguard.obf.config.ConfigurationGroup;
import me.bannock.donutguard.obf.config.impl.ConfigKeyBoolean;

public class IndyConfigGroup extends ConfigurationGroup {

    public static final ConfigKeyBoolean REPLACE_WITH_INDY_ENABLED =
            new ConfigKeyBoolean("Replace with invoke dynamic enabled", false);

}
