package me.bannock.donutguard.obf;

import com.google.inject.AbstractModule;
import me.bannock.donutguard.obf.job.ObfuscatorJob;
import me.bannock.donutguard.obf.job.ObfuscatorJobFactory;


public class ObfuscatorModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new com.google.inject.assistedinject.FactoryModuleBuilder()
                .implement(ObfuscatorJob.class, ObfuscatorJob.class)
                .build(ObfuscatorJobFactory.class));
    }

}
