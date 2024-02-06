package me.bannock.donutguard.obf.job;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.obf.asm.JarHandler;
import me.bannock.donutguard.obf.mutator.Mutator;
import me.bannock.donutguard.obf.mutator.impl.NopSpammerMutator;
import me.bannock.donutguard.obf.mutator.impl.TestMutator;
import me.bannock.donutguard.obf.mutator.impl.string.LineNumberStringLiteralMutator;

public class JobModule extends AbstractModule {

    private final ObfuscatorJob job;

    public JobModule(ObfuscatorJob job) {
        this.job = job;
    }

    @Override
    protected void configure() {
        bind(ObfuscatorJob.class).toInstance(job);
        bindMutators();
    }

    private void bindMutators(){
        Multibinder<Mutator> mutatorMultibinder = Multibinder.newSetBinder(binder(), Mutator.class);
        mutatorMultibinder.addBinding().to(NopSpammerMutator.class);
        mutatorMultibinder.addBinding().to(LineNumberStringLiteralMutator.class);

        mutatorMultibinder.addBinding().to(TestMutator.class); // It's for testing so it's last
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
