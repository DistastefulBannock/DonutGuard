package me.bannock.donutguard.obf;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;

/**
 * A module for the Obfuscator that allows you to pass in a
 * provider to be called. This could be used to better fit your
 * implementation
 */
public class CustomObfuscatorModule extends AbstractModule {

    private final Provider<ConfigDTO> configProvider;

    /**
     * @param configProvider A provider that will be called to provide
     *                       the config data transfer object
     */
    public CustomObfuscatorModule(Provider<ConfigDTO> configProvider){
        this.configProvider = configProvider;
    }

    @Provides
    public ConfigDTO provideConfig(){
        return this.configProvider.get();
    }

}
