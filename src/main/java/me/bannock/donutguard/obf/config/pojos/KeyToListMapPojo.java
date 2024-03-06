package me.bannock.donutguard.obf.config.pojos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class KeyToListMapPojo implements Serializable {

    private static final long serialVersionUID = 3621L;

    private final HashMap<String, ArrayList<String>> mappings;

    public KeyToListMapPojo(HashMap<String, ArrayList<String>> defaultMappings){
        this.mappings = defaultMappings;
    }

    /**
     * @param key The key to get the values for
     * @return The values for the provided key; never null
     */
    public List<String> getValues(String key){
        ArrayList<String> values = this.mappings.get(key);
        if (values == null)
            values = new ArrayList<>();
        return Collections.unmodifiableList(values);
    }

    /**
     * Adds a value to the provided key's list of values
     * @param key The key to add the value to
     * @param value The value to add
     */
    public void add(String key, String value){
        if (!this.mappings.containsKey(key))
            this.mappings.put(key, new ArrayList<>());
        this.mappings.get(key).add(value);
    }

    /**
     * Removes a value from the provided key's list of values
     * @param key The key to remove the value from
     * @param value The value to remove
     */
    public void remove(String key, String value){
        if (!this.mappings.containsKey(key))
            return;
        this.mappings.get(key).remove(value);
    }

    /**
     * Clears all values for a key
     * @param key The key to wipe the values for
     */
    public void clear(String key){
        this.mappings.remove(key);
    }

    /**
     * Clears every key and value from the mappings
     */
    public void clearAll(){
        this.mappings.clear();
    }

    public HashMap<String, ArrayList<String>> getMappings() {
        return mappings;
    }

}
