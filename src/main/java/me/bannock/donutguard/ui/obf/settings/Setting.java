package me.bannock.donutguard.ui.obf.settings;

import me.bannock.donutguard.obf.ConfigDTO;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to quickly and easily create different menus for each obfuscator setting
 * @param <T> This is the type that this setting will use
 */
public abstract class Setting<T> {

    private final String name;
    private final List<SettingFilter<T>> filters;
    private final ConfigDTO config;
    private final Field field;
    private T value;

    /**
     * Creates a new setting
     * @param name The name of the setting
     * @param config The config data transfer object
     * @param fieldName The name of the field in the config DTO that this setting is for
     */
    public Setting(String name, ConfigDTO config, T value, String fieldName){
        this.name = name;
        this.filters = new ArrayList<>();
        this.config = config;
        this.value = value;
        try {
            field = ConfigDTO.class.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("The field name \"" + fieldName +
                    "\" does not exist in the config DTO class", e);
        }
    }

    /**
     * Creates a new swing component for this setting and returns it
     * @return The component that was created
     */
    public abstract JComponent createAndGetComponent();

    /**
     * Sets the value of the setting as well as the assigned config field
     * @param value The value to set
     * @return True if the value was set, otherwise false
     */
    public boolean setValue(T value) {
        for (SettingFilter<T> filter : filters) {
            if(!filter.filter(this.value, value)){
                JOptionPane.showInputDialog(null, "Invalid value",
                        "You've inputted an invalid value");
                return false;
            }
        }
        try {
            field.setAccessible(true);
            field.set(config, value);
        } catch (IllegalAccessException e) {
            return false;
        }
        this.value = value;
        return true;
    }

    /**
     * @return The name of this setting
     */
    public String getName() {
        return name;
    }

    /**
     * Adds a filter to this setting. It is called when the value is set by the user
     * @param filter The filter to add
     */
    public void addFilter(SettingFilter<T> filter){
        filters.add(filter);
    }

    /**
     * Removes a filter from this setting
     * @param filter the filter to remove
     */
    public void removeFilters(SettingFilter<T> filter){
        filters.remove(filter);
    }

    /**
     * Removes all filters from the setting
     */
    public void clearFilters(){
        filters.clear();
    }

    /**
     * @return The value of the setting
     */
    public T getValue() {
        return value;
    }

    /**
     * @return The config object reference
     */
    protected ConfigDTO getConfig() {
        return config;
    }
}
