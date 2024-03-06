package me.bannock.donutguard.obf.config.impl;

import me.bannock.donutguard.obf.config.ConfigKey;
import me.bannock.donutguard.obf.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class ConfigKeyInteger extends ConfigKey<String> {

    private final Logger logger = LogManager.getLogger();
    private final int defaultValue;

    public ConfigKeyInteger(String name, Integer defaultValue) {
        super(name, String.valueOf(defaultValue));
        this.defaultValue = defaultValue;
    }

    /**
     * @param config The configuration to get the values from
     * @return The value, or the default value if the value inside
     *         the config isn't invalid
     */
    public int getInt(Configuration config){
        Objects.requireNonNull(config);
        try{
            return Integer.parseInt(get(config));
        }catch (Throwable e){
            logger.warn("Value on config was invalid. Returning default", e);
            return this.defaultValue;
        }
    }

    /**
     * Sets a value in a config
     * @param config The config to set the values for
     * @param val The value to set
     */
    public void setInt(Configuration config, int val){
        Objects.requireNonNull(config);
        set(config, String.valueOf(val));
    }

}
