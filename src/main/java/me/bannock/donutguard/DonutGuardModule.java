package me.bannock.donutguard;

import com.google.inject.AbstractModule;
import me.bannock.donutguard.views.MainFrame;

public class DonutGuardModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(DonutGuard.class).asEagerSingleton();
        bind(MainFrame.class).asEagerSingleton();
    }

}
