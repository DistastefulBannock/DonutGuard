package me.bannock.donutguard.obf.config;

import java.io.Serializable;
import java.util.Objects;

public abstract class ConfigKey<T extends Serializable> {

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
    protected T get(Configuration configuration){
        if (group == null)
            throw new IllegalStateException("This key has not yet been assigned to a group");
        Objects.requireNonNull(configuration);
        return configuration.get(group, name, defaultValue);
    }

    /**
     * Sets the value for a key in a configuration object
     * @param configuration The configuration object to set the value to
     * @param value The value to set in the configuration object
     */
    protected void set(Configuration configuration, T value){
        if (group == null)
            throw new IllegalStateException("This key has not yet been assigned to a group");
        Objects.requireNonNull(configuration);
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

    /**
     * A proxy method that grants subclasses the ability to call
     * the equivalent protected method inside of Configuration.
     * Gets a value from the key map
     * @param config The configuration to proxy into
     * @param group The group that the key is in
     * @param key The name of the key
     * @param typeInstance An instance of the type that the value for the key is
     * @return The value for this key. Will throw exception if value is null
     * @param <Q> The type that the value for this key is
     */
    @SuppressWarnings("unchecked")
    protected <Q> Q proxyGet(Configuration config, ConfigurationGroup group,
                        String key, Q typeInstance){
        Objects.requireNonNull(typeInstance);
        return (Q) config.get(group, key, typeInstance.getClass());
    }

    /**
     * A proxy method that grants subclasses the ability to call
     * the equivalent protected method inside of Configuration.
     * Sets a key in the key map
     * @param config The configuration to proxy into
     * @param group The group that the key is in
     * @param key The name of the key
     * @param value The value for the key
     * @param <Q> The type of the value for the key
     */
    protected <Q> void proxySet(Configuration config, ConfigurationGroup group,
                                String key, Q value){
        config.set(group, key, value);
    }

    public String getName() {
        return name;
    }

    public ConfigurationGroup getGroup() {
        return group;
    }

}
