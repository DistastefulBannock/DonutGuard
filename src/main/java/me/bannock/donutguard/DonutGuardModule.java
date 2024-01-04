package me.bannock.donutguard;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.views.MainFrame;

public class DonutGuardModule extends AbstractModule {

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

}
