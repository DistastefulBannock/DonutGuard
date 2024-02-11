package me.bannock.donutguard.obf.config;

import me.bannock.donutguard.obf.config.special.ConfigKeyMultiMap;
import me.bannock.donutguard.obf.dictionary.Dictionary;
import me.bannock.donutguard.obf.mutator.impl.string.StringLiteralEncryptionType;

import java.io.File;
import java.util.ArrayList;

public class DefaultConfigGroup extends ConfigurationGroup {

    public static final ConfigKey<File> INPUT =
            new ConfigKey<>("Input", new File("input.jar"));
    public static final ConfigKey<File> OUTPUT =
            new ConfigKey<>("Output", new File("output.jar"));

    public static final ConfigKey<Boolean> COMPUTE_FRAMES =
            new ConfigKey<>("Comepute frames", false);
    public static final ConfigKey<Boolean> COMPUTE_MAXES =
            new ConfigKey<>("Comepute maxes", true);

    public static final ConfigKey<Boolean> SUPPRESS_DUPE_NODE_ERRORS =
            new ConfigKey<>("Suppress duplicate node errors", true);

    public static final ConfigKey<ArrayList<File>> LIBRARIES =
            new ConfigKey<>("libraries", new ArrayList<>());
    public static final ConfigKey<Boolean> INCLUDE_LIBS_IN_OUTPUT =
            new ConfigKey<>("Include libs in output", false);

    public static final ConfigKeyMultiMap<String, String> BLACKLIST =
            new ConfigKeyMultiMap<>("Blacklist");
    public static final ConfigKeyMultiMap<String, String> WHITELIST =
            new ConfigKeyMultiMap<>("Whitelist");

    public static final ConfigKey<Dictionary> LOCAL_VAR_DICT =
            new ConfigKey<>("Local var dict", Dictionary.ALPHABET);
    public static final ConfigKey<Dictionary> METHOD_DICT =
            new ConfigKey<>("Method dict", Dictionary.ALPHABET);
    public static final ConfigKey<Dictionary> FIELD_DICT =
            new ConfigKey<>("Field dict", Dictionary.ALPHABET);
    public static final ConfigKey<Dictionary> CLASS_DICT =
            new ConfigKey<>("Class dict", Dictionary.ALPHABET);
    public static final ConfigKey<Dictionary> PACKAGE_DICT =
            new ConfigKey<>("Package dict", Dictionary.ALPHABET);
    public static final ConfigKey<Integer> NESTED_PACKAGES =
            new ConfigKey<>("Nested package count", 2);

    public static final ConfigKey<Boolean> DEV_TEST_MUTATOR_ENABLED =
            new ConfigKey<>("Dev test mutator enabled", false);

    public static final ConfigKey<Boolean> NOP_SPAM_ENABLED =
            new ConfigKey<>("NOP spammer enabled", false);
    public static final ConfigKey<Integer> NOP_SPAM_COUNT =
            new ConfigKey<>("NOP spam count", 2);

    public static final ConfigKey<Boolean> STRING_ENC_ENABLED =
            new ConfigKey<>("String enc enabled", false);
    public static final ConfigKey<StringLiteralEncryptionType> STRING_ENC_TYPE =
            new ConfigKey<>("String enc type",
                    StringLiteralEncryptionType.IDENTIFIERS_VIA_LINE_NUMBERS_AND_INTS);

}
