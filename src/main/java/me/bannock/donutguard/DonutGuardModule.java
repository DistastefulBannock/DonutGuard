package me.bannock.donutguard;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Provides;
import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.obf.ObfuscatorModule;
import me.bannock.donutguard.obf.Obfuscator;
import me.bannock.donutguard.ui.MainFrame;

public class DonutGuardModule extends AbstractModule {

    private Obfuscator obfuscatorInstance = null;

    @Override
    protected void configure() {
        bind(DonutGuard.class).asEagerSingleton();
        bind(MainFrame.class).asEagerSingleton();
    }

    @Inject
    @Provides
    public ConfigDTO provideConfig(DonutGuard donutGuard){
        return donutGuard.getConfig();
    }

    @Inject
    @Provides
    public Obfuscator provideObfuscator(Provider<ConfigDTO> provider){
        if (this.obfuscatorInstance == null)
            this.obfuscatorInstance = Guice.createInjector(new ObfuscatorModule(provider))
                    .getInstance(Obfuscator.class);
        return obfuscatorInstance;
    }

}
