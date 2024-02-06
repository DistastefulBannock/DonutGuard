package me.bannock.donutguard.obf.config;

import java.io.Serializable;
import java.util.HashMap;

public class ConfigKeyMap<Q extends Serializable, E extends Serializable> extends ConfigKey<HashMap<Q, E>> {
    public ConfigKeyMap(String name, HashMap<Q, E> defaultMap) {
        super(name, defaultMap);
    }

    /**
     * Puts a key/value combo on this key's map
     * @param config The configuration to use when putting the key/value pair
     * @param key The key to put on the map
     * @param value The value to assign to the key
     */
    public void put(Configuration config, Q key, E value){
        HashMap<Q, E> map = get(config);
        if (map == null)
            throw new IllegalStateException("Current map is null");
        map.put(key, value);
    }

    /**
     * Gets a value from the map as opposed to the key
     * @param config The configuration to use when pulling the value
     * @param key The key used to find the value
     * @return The value for this key if one is set, otherwise null;
     */
    public E get(Configuration config, Q key){
        HashMap<Q, E> map = get(config);
        if (map == null)
            return null;
        return map.get(key);
    }

}
