package me.bannock.donutguard.obf.mutator.impl.string;

import me.bannock.donutguard.obf.config.ConfigKey;
import me.bannock.donutguard.obf.config.ConfigurationGroup;

public class StringEncConfigGroup extends ConfigurationGroup {

    public static final ConfigKey<Boolean> STRING_ENC_ENABLED =
            new ConfigKey<>("String enc enabled", false);

    public static final ConfigKey<StringLiteralEncryptionType> STRING_ENC_TYPE =
            new ConfigKey<>("String enc type",
                    StringLiteralEncryptionType.IDENTIFIERS_VIA_LINE_NUMBERS_AND_INTS);

}
