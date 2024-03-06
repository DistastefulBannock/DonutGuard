package me.bannock.donutguard.obf.mutator.impl.string;

import me.bannock.donutguard.obf.config.ConfigurationGroup;
import me.bannock.donutguard.obf.config.impl.ConfigKeyBoolean;
import me.bannock.donutguard.obf.config.impl.ConfigKeyEnum;

public class StringEncConfigGroup extends ConfigurationGroup {

    public static final ConfigKeyBoolean STRING_ENC_ENABLED =
            new ConfigKeyBoolean("String enc enabled", false);

    public static final ConfigKeyEnum<StringLiteralEncryptionType> STRING_ENC_TYPE =
            new ConfigKeyEnum<>("String enc type",
                    StringLiteralEncryptionType.IDENTIFIERS_VIA_LINE_NUMBERS_AND_INTS);

}
