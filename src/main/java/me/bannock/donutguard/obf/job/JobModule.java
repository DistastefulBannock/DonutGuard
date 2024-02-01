package me.bannock.donutguard.obf.job;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.obf.asm.JarHandler;

public class JobModule extends AbstractModule {

    private final ObfuscatorJob job;

    public JobModule(ObfuscatorJob job) {
        this.job = job;
    }

    @Override
    protected void configure() {
        bind(ObfuscatorJob.class).toInstance(job);
    }

    @Inject
    @Provides
    public ConfigDTO provideConfigDTO() {
        return job.getConfigDTO();
    }

    @Inject
    @Provides
    public JarHandler provideJarHandler(){
        return job.getJarHandler();
    }

}
