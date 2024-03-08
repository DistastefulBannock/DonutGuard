package me.bannock.donutguard.obf.config;

import com.google.gson.Gson;
import com.google.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Configuration implements Serializable {

    private static final long serialVersionUID = 3621L;

    private transient final Logger logger = LogManager.getLogger();

    private final Map<String, Object> keys;

    @Inject
    public Configuration(Set<ConfigurationGroup> groups){
        this.keys = new LinkedHashMap<>();
        logger.info("Loading configuration groups...");
        for (ConfigurationGroup group : groups){
            String groupId = group.getClass().getName();
            logger.info(String.format("Loading group \"%s\"...", groupId));
            if (keys.containsKey(groupId))
                throw new IllegalArgumentException("Your configuration group class path must be unique.");
            group.loadConfigurationKeys(this);
            logger.info("Successfully loaded group");
        }
        logger.info("Successfully loaded configuration groups");
    }

    /**
     * DO NOT CALL THIS CONSTRUCTOR.
     * It is used by special classes that load data for this class.
     * If you use this to create a config then no values will be
     * initialized unless done via reflection. You should always
     * create this object with a guice injector.
     */
    public Configuration(){
        this.keys = new LinkedHashMap<>();
    }

    /**
     * Gets a value from the key map
     * @param group The group that the key is in
     * @param key The name of the key
     * @param type The type that the value for the key is
     * @return The value for this key. Will throw exception if value is null
     * @param <T> The type that the value for this key is
     */
    protected <T> T get(ConfigurationGroup group, String key, Class<T> type){
        Objects.requireNonNull(group);
        Objects.requireNonNull(key);
        Objects.requireNonNull(type);
        String keyInMap = group.getClass().getName() + "." + key;
        Object value = keys.get(keyInMap);
        if (value == null)
            throw new IllegalArgumentException(String.format("No value found for \"%s\"", keyInMap));
        if (!type.isInstance(value)) {
            if (String.class.isAssignableFrom(type))
                value = new Gson().toJson(value);
            else
                throw new ClassCastException(String.format("\"%s\" is not of type \"%s\". ", value, type.getName()));
        }
        return type.cast(value);
    }

    /**
     * Gets a value from the key map
     * @param group The group that the key is in
     * @param key The name of the key
     * @param typeInstance An instance of the type that the value for the key is
     * @return The value for this key. Will throw exception if value is null
     * @param <T> The type that the value for this key is
     */
    @SuppressWarnings("unchecked")
    protected <T> T get(ConfigurationGroup group, String key, T typeInstance){
        Objects.requireNonNull(typeInstance);
        return (T) get(group, key, typeInstance.getClass());
    }

    /**
     * Sets a key in the key map
     * @param group The group that the key is in
     * @param key The name of the key
     * @param value The value for the key
     * @param <T> The type of the value for the key
     */
    protected <T> void set(ConfigurationGroup group, String key, T value){
        String keyInMap = group.getClass().getName() + "." + key;
        keys.put(keyInMap, value);
    }

    /**
     * Pulls all key/value pairs from another configuration
     * and merges them into this one
     * @param configuration The configuration to pull data
     *                      from
     */
    public void merge(Configuration configuration){
        this.keys.putAll(configuration.getKeys());
    }

    protected Map<String, Object> getKeys() {
        return keys;
    }

}
