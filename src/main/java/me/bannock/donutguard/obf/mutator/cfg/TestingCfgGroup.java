package me.bannock.donutguard.obf.mutator.cfg;

import me.bannock.donutguard.obf.config.ConfigKey;
import me.bannock.donutguard.obf.config.ConfigurationGroup;

public class TestingCfgGroup extends ConfigurationGroup {

    public static final ConfigKey<Boolean> DEV_TEST_MUTATOR_ENABLED =
            new ConfigKey<>("Dev test mutator enabled", false);

}
