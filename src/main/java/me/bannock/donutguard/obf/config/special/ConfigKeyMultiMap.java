package me.bannock.donutguard.obf.config.special;

import me.bannock.donutguard.obf.config.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.Serializable;

public class ConfigKeyMultiMap<Q extends Serializable, E extends Serializable> extends ConfigKeyMap<Q, ArrayList<E>> {

    /**
     * @param name The name of the key
     * @param defaultMap The default map object to use with this key
     */
    public ConfigKeyMultiMap(String name, HashMap<Q, ArrayList<E>> defaultMap) {
        super(name, defaultMap);
    }

    /**
     * @param name The name of the key
     */
    public ConfigKeyMultiMap(String name) {
        this(name, new HashMap<>());
    }

    /**
     * Adds a value to one of the keys
     * @param config The configuration to use when fetching/adding the values
     * @param key The key to use with the map
     * @param value The value to add to the key's values
     * @return True if the value was added, otherwise false
     */
    public boolean add(Configuration config, Q key, E value){
        HashMap<Q, ArrayList<E>> map = get(config);
        if (map == null)
            return false;
        ArrayList<E> mappedTo = map.computeIfAbsent(key, k -> new ArrayList<>());
        return mappedTo.add(value);
    }

    /**
     * @param config The configuration to use when fetching/removing the values
     * @param key The key to use with the map
     * @param value The value to remove from the key's values
     * @return True if the value was removed, otherwise false
     */
    public boolean remove(Configuration config, Q key, E value){
        HashMap<Q, ArrayList<E>> map = get(config);
        if (map == null)
            return false;
        ArrayList<E> mappedTo = map.get(key);
        if (mappedTo == null)
            return false;
        boolean removed = mappedTo.remove(value);
        if (removed && mappedTo.isEmpty())
            map.remove(key);
        return removed;
    }

}
