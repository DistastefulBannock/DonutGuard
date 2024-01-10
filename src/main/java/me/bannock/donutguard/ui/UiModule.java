package me.bannock.donutguard.ui;

import com.google.inject.AbstractModule;
import me.bannock.donutguard.ui.jobs.models.JobsViewModel;
import me.bannock.donutguard.ui.jobs.models.JobsViewModelImpl;
import me.bannock.donutguard.ui.obf.models.ObfuscatorModel;
import me.bannock.donutguard.ui.obf.models.ObfuscatorModelImpl;

public class UiModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ObfuscatorModel.class).to(ObfuscatorModelImpl.class);
        bind(JobsViewModel.class).to(JobsViewModelImpl.class);
    }

}
