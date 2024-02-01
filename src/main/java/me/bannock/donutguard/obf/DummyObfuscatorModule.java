package me.bannock.donutguard.obf;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import me.bannock.donutguard.obf.asm.JarHandler;

public class DummyObfuscatorModule extends AbstractModule {

    private final ConfigDTO config;

    public DummyObfuscatorModule(ConfigDTO config){
        this.config = config;
    }

    @Override
    protected void configure() {
        bind(JarHandler.class).asEagerSingleton();
    }

    @Provides
    public ConfigDTO provideConfig(){
        return this.config;
    }

}
