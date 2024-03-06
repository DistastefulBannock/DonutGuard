package me.bannock.donutguard.obf.config.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import me.bannock.donutguard.obf.config.ConfigKey;
import me.bannock.donutguard.obf.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.Objects;

public class ConfigKeyObject<T extends Serializable> extends ConfigKey<T> {

    private static final Gson gsonInstance = new Gson();

    private final Logger logger = LogManager.getLogger();

    public ConfigKeyObject(String name, T defaultValue) {
        super(name, defaultValue);
    }

    public T getObj(Configuration config){
        return get(config);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected T get(Configuration configuration) {
        if (getGroup() == null)
            throw new IllegalStateException("This key has not yet been assigned to a group");
        Objects.requireNonNull(configuration);
        try{
            return proxyGet(configuration, getGroup(), getName(), getDefaultValue());
        }catch (ClassCastException e){

            // If the type did not match then it's likely still serialized in a json string,
            // so we use gson to deserialize it
            Object val;
            try{
                val = gsonInstance.fromJson(
                        proxyGet(configuration, getGroup(), getName(), ""),
                        getDefaultValue().getClass()
                );
            }catch (JsonSyntaxException e1){
                // This means that something went wrong while gson was deserializing
                // the string value. In this case we return default
                logger.error("Failed to parse json. Returning default object", e);
                return getDefaultValue();
            }

            if (!getDefaultValue().getClass().isInstance(val))
                throw new ClassCastException("Value is not instance of generic");
            // Set value again to correct broken mapping
            T castedVal = (T) val;
            set(configuration, castedVal);
            return castedVal;
        }
    }

    @Override
    protected void set(Configuration configuration, T value) {
        super.set(configuration, value);
    }

}
