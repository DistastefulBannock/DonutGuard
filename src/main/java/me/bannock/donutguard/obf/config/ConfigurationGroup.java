package me.bannock.donutguard.obf.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;


public abstract class ConfigurationGroup {

    private final Logger logger = LogManager.getLogger();

    /**
     * Loads the configuration keys to a specific configuration instance
     */
    protected void loadConfigurationKeys(Configuration configuration){

        // We first need to get all static config keys from the class. We use reflection
        // so the user doesn't need to edit any logic or mess with methods.
        List<Field> keyFields = new ArrayList<>();
        for (Field field : getClass().getDeclaredFields()) {
            if (!ConfigKey.class.isAssignableFrom(field.getType()))
                continue;
            if (!Modifier.isStatic(field.getModifiers())){
                logger.warn(String.format("ConfigKey \"%s\" must be static to " +
                        "be entered into a configuration.", field.getName()));
                continue;
            }
            else if (!Modifier.isFinal(field.getModifiers())){
                logger.warn(String.format("ConfigKey \"%s\" must be final to " +
                        "be entered into a configuration.", field.getName()));
                continue;
            }
            keyFields.add(field);
        }


        // With the fields, we're able to get the keys themselves.
        // We have the keys set their values in our configuration
        for (Field field : keyFields){
           try{
               if (field.get(null) == null){
                   logger.warn("Config key must not be null to be used.");
                   continue;
               }
               ConfigKey<?> key = ((ConfigKey<?>)field.get(null));
               try{
                   key.assignGroup(this);
               }catch (IllegalStateException ignored){} // We don't really care
               key.setObject(configuration, key.getDefaultValue());
           }catch (IllegalAccessException e){
               logger.warn("Could not access key field. Please make sure that it is public.", e);
           }
        }

    }

    /**
     * Can be overridden if desired
     * @return The friendly name for this config group
     */
    public String getFriendlyName(){
        return getClass().getSimpleName();
    }

}
