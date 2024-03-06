package me.bannock.donutguard.obf.config.impl;

import me.bannock.donutguard.obf.config.ConfigKey;
import me.bannock.donutguard.obf.config.Configuration;

public class ConfigKeyString extends ConfigKey<String> {
    public ConfigKeyString(String name, String defaultValue) {
        super(name, defaultValue);
    }

    /**
     * Gets the value for this key from a configuration
     * @param config The configuration to pull the value from
     * @return The value for this key
     */
    public String getString(Configuration config){
        return get(config);
    }

    /**
     * Sets the value for this key in a configuration
     * @param config The configuration to set this value for
     * @param val The value to set in the configuration
     */
    public void setString(Configuration config, String val){
        set(config, val);
    }

}
