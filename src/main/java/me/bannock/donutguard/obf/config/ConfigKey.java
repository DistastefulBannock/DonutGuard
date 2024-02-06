package me.bannock.donutguard.obf.config;

import java.io.Serializable;
import java.util.Objects;

public class ConfigKey<T extends Serializable> {

    private final String name;
    private final T defaultValue;
    private ConfigurationGroup group;

    public ConfigKey(String name, T defaultValue){
        Objects.requireNonNull(name);
        Objects.requireNonNull(defaultValue);
        this.name = name;
        this.defaultValue = defaultValue;
    }

    /**
     * Assigns this key to a group
     * @param group The group to assign this key to
     */
    protected void assignGroup(ConfigurationGroup group){
        if (this.group != null)
            throw new IllegalStateException("This key has already been assigned to a group");
        this.group = group;
    }

    protected T getDefaultValue(){
        return defaultValue;
    }

    /**
     * Gets the value for this key from a specific configuration
     * @param configuration The configuration to get the value from
     * @return The value for this key
     */
    public T get(Configuration configuration){
        if (group == null)
            throw new IllegalStateException("This key has not yet been assigned to a group");
        return configuration.get(group, name, defaultValue);
    }

    /**
     * Sets the value for a key in a configuration object
     * @param configuration The configuration object to set the value to
     * @param value The value to set in the configuration object
     */
    public void set(Configuration configuration, T value){
        if (group == null)
            throw new IllegalStateException("This key has not yet been assigned to a group");
        configuration.set(group, name, value);
    }

    /**
     * Same as the set method but used if the type of this key is unknown. Will crash if used incorrectly.
     * @param configuration The configuration object to set the value to
     * @param value The value to set in the configuration object
     */
    @SuppressWarnings("unchecked")
    protected void setObject(Configuration configuration, Object value){
        set(configuration, (T)value);
    }

}
