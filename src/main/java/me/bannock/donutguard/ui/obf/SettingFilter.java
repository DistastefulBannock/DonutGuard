package me.bannock.donutguard.ui.obf;

/**
 * A filter for Setting objects
 * @param <T> The type that the setting is using for the value
 */
public interface SettingFilter<T> {

    boolean filter(T oldValue, T newValue);

}
