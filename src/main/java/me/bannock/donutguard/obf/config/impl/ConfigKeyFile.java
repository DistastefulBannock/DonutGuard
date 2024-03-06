package me.bannock.donutguard.obf.config.impl;

import me.bannock.donutguard.obf.config.ConfigKey;
import me.bannock.donutguard.obf.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Objects;

public class ConfigKeyFile extends ConfigKey<String> {

    private final Logger logger = LogManager.getLogger();

    public ConfigKeyFile(String name, File defaultValue) {
        super(name, defaultValue.getAbsolutePath());
    }

    /**
     * @param config The configuration to grab the file from
     * @return The file object, or null if no path is stored
     */
    public File getFile(Configuration config){
        String path = get(config);
        if (path == null){
            logger.warn(String.format("No path was found for \"%s\" ConfigKeyFile", getName()));
            return null;
        }
        return new File(path);
    }

    /**
     * Sets the file path used in the config
     * @param config The configuration to set the path in
     * @param file The file to set the path to
     */
    public void setFile(Configuration config, File file){
        Objects.requireNonNull(config);
        Objects.requireNonNull(file);
        set(config, file.getAbsolutePath());
    }

}
