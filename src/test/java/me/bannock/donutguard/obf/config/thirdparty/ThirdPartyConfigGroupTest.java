package me.bannock.donutguard.obf.config.thirdparty;

import me.bannock.donutguard.obf.config.ConfigurationGroup;
import me.bannock.donutguard.obf.config.impl.ConfigKeyBoolean;

public class ThirdPartyConfigGroupTest extends ConfigurationGroup {

    public static final ConfigKeyBoolean THIRD_PARTY_MUTATOR_ON =
            new ConfigKeyBoolean("Mutator on", true);

}
