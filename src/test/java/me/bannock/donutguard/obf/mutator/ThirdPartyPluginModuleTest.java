package me.bannock.donutguard.obf.mutator;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class ThirdPartyPluginModuleTest extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder<Mutator> mutatorMultibinder = Multibinder.newSetBinder(binder(), Mutator.class);
        mutatorMultibinder.addBinding().to(ThirdPartyMutatorTest.class);
    }

}