package me.bannock.donutguard;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import me.bannock.donutguard.ui.jobs.models.JobsViewModel;
import me.bannock.donutguard.ui.jobs.models.JobsViewModelImpl;
import me.bannock.donutguard.ui.obf.models.ObfuscatorModel;
import me.bannock.donutguard.ui.obf.models.ObfuscatorModelImpl;
import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.obf.Obfuscator;
import me.bannock.donutguard.ui.MainFrame;

public class DonutGuardModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(DonutGuard.class).asEagerSingleton();
        bind(MainFrame.class).asEagerSingleton();
        bind(Obfuscator.class).asEagerSingleton();
        bind(ObfuscatorModel.class).to(ObfuscatorModelImpl.class);
        bind(JobsViewModel.class).to(JobsViewModelImpl.class);
    }

    @Inject
    @Provides
    public ConfigDTO provideConfig(DonutGuard donutGuard){
        return donutGuard.getConfig();
    }

}
