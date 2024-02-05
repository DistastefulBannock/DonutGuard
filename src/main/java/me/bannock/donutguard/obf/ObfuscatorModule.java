package me.bannock.donutguard.obf;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;


public class ObfuscatorModule extends AbstractModule {

    private final Provider<ConfigDTO> configProvider;

    /**
     * @param configProvider A provider that will be called to provide
     *                       the config data transfer object
     */
    public ObfuscatorModule(Provider<ConfigDTO> configProvider){
        this.configProvider = configProvider;
    }

    public ObfuscatorModule(ConfigDTO configInstance){
        this.configProvider = () -> configInstance;
    }

    @Provides
    public ConfigDTO provideConfig(){
        return this.configProvider.get();
    }

}
