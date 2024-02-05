package me.bannock.donutguard.obf;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Simple module for Obfuscator. Takes in one configuration object
 * and only uses it.
 */
public class SimpleObfuscatorModule extends AbstractModule {

    private final ConfigDTO config;

    public SimpleObfuscatorModule(ConfigDTO config){
        this.config = config;
    }

    @Provides
    public ConfigDTO provideConfig(){
        return this.config;
    }

}
