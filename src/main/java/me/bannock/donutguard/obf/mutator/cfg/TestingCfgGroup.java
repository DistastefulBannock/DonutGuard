package me.bannock.donutguard.obf.mutator.cfg;

import me.bannock.donutguard.obf.config.ConfigurationGroup;
import me.bannock.donutguard.obf.config.impl.ConfigKeyBoolean;

public class TestingCfgGroup extends ConfigurationGroup {

    public static final ConfigKeyBoolean DEV_TEST_MUTATOR_ENABLED =
            new ConfigKeyBoolean("Dev test mutator enabled", false);

}
