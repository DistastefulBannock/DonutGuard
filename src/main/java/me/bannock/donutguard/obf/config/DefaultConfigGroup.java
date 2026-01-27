package me.bannock.donutguard.obf.config;

import me.bannock.donutguard.obf.config.impl.ConfigKeyBoolean;
import me.bannock.donutguard.obf.config.impl.ConfigKeyEnum;
import me.bannock.donutguard.obf.config.impl.ConfigKeyFile;
import me.bannock.donutguard.obf.config.impl.ConfigKeyInteger;
import me.bannock.donutguard.obf.config.impl.ConfigKeyObject;
import me.bannock.donutguard.obf.config.pojos.KeyToListMapPojo;
import me.bannock.donutguard.obf.dictionary.Dictionary;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class DefaultConfigGroup extends ConfigurationGroup {

    public static final ConfigKeyFile INPUT =
            new ConfigKeyFile("Input", new File("input.jar"));
    public static final ConfigKeyFile OUTPUT =
            new ConfigKeyFile("Output", new File("output.jar"));

    public static final ConfigKeyBoolean COMPUTE_FRAMES =
            new ConfigKeyBoolean("Compute frames", false);
    public static final ConfigKeyBoolean COMPUTE_MAXES =
            new ConfigKeyBoolean("Compute maxes", true);

    public static final ConfigKeyBoolean SUPPRESS_DUPE_NODE_ERRORS =
            new ConfigKeyBoolean("Suppress duplicate node errors", true);

    public static final ConfigKeyObject<ArrayList<File>> LIBRARIES =
            new ConfigKeyObject<>("libraries", new ArrayList<>());
    /**
     * Classes from mutated libs are disposed of if not included in output
     */
    public static final ConfigKeyBoolean MUTATE_LIBS =
            new ConfigKeyBoolean("Mutate libs", false);
    public static final ConfigKeyBoolean INCLUDE_LIBS_IN_OUTPUT =
            new ConfigKeyBoolean("Include libs in output", false);

    public static final ConfigKeyObject<KeyToListMapPojo> BLACKLIST =
            new ConfigKeyObject<>("Blacklist", new KeyToListMapPojo(new HashMap<>()));
    public static final ConfigKeyObject<KeyToListMapPojo> WHITELIST =
            new ConfigKeyObject<>("Whitelist", new KeyToListMapPojo(new HashMap<>()));

    public static final ConfigKeyEnum<Dictionary> LOCAL_VAR_DICT =
            new ConfigKeyEnum<>("Local var dict", Dictionary.ALPHABET){};
    public static final ConfigKeyEnum<Dictionary> METHOD_DICT =
            new ConfigKeyEnum<>("Method dict", Dictionary.ALPHABET){};
    public static final ConfigKeyEnum<Dictionary> FIELD_DICT =
            new ConfigKeyEnum<>("Field dict", Dictionary.ALPHABET){};
    public static final ConfigKeyEnum<Dictionary> CLASS_DICT =
            new ConfigKeyEnum<>("Class dict", Dictionary.ALPHABET){};
    public static final ConfigKeyEnum<Dictionary> PACKAGE_DICT =
            new ConfigKeyEnum<>("Package dict", Dictionary.ALPHABET){};
    public static final ConfigKeyInteger NESTED_PACKAGES =
            new ConfigKeyInteger("Nested package count", 2);

    public static final ConfigKeyBoolean DEV_TEST_MUTATOR_ENABLED
            = new ConfigKeyBoolean("Dev test mutator enabled", false);

}
