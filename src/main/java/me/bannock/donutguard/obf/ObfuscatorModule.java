package me.bannock.donutguard.obf;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import me.bannock.donutguard.obf.job.ObfuscatorJob;
import me.bannock.donutguard.obf.job.ObfuscatorJobFactory;
import me.bannock.donutguard.obf.config.ConfigurationGroup;
import me.bannock.donutguard.obf.config.DonutGuardConfigGroup;
import org.apache.logging.log4j.ThreadContext;


public class ObfuscatorModule extends AbstractModule {

    public ObfuscatorModule(){
        if (ThreadContext.get("threadId") == null)
            ThreadContext.put("threadId", "Independent Start");
    }

    @Override
    protected void configure() {
        install(new com.google.inject.assistedinject.FactoryModuleBuilder()
                .implement(ObfuscatorJob.class, ObfuscatorJob.class)
                .build(ObfuscatorJobFactory.class));
        bindConfiguration();
    }

    private void bindConfiguration(){
        Multibinder<ConfigurationGroup> configurationGroupMultibinder =
                Multibinder.newSetBinder(binder(), ConfigurationGroup.class);
        configurationGroupMultibinder.addBinding().to(DonutGuardConfigGroup.class);
    }

}
