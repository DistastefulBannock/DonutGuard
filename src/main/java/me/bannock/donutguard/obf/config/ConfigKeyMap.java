package me.bannock.donutguard.obf.config;

import java.io.Serializable;
import java.util.HashMap;

public class ConfigKeyMap<Q extends Serializable, E extends Serializable, T extends HashMap<Q, E>> extends ConfigKey<T> {
    public ConfigKeyMap(String name, T defaultMap) {
        super(name, defaultMap);
    }

    public void put(Configuration config, Q key, E value){
        HashMap<Q, E> map = get(config);
        if (map == null)
            throw new IllegalStateException("Current map is null");
    }

}
