package me.bannock.donutguard.obf.config.thirdparty;

import me.bannock.donutguard.obf.config.ConfigKey;
import me.bannock.donutguard.obf.config.ConfigurationGroup;

public class ThirdPartyConfigGroupTest extends ConfigurationGroup {

    public static final ConfigKey<Boolean> THIRD_PARTY_MUTATOR_ON = new ConfigKey<>("Mutator on", true);

}
