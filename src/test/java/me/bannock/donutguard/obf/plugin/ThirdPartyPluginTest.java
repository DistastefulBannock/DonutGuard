package me.bannock.donutguard.obf.plugin;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import me.bannock.donutguard.obf.mutator.Mutator;

public class ThirdPartyPluginTest extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder<Mutator> mutatorMultibinder = Multibinder.newSetBinder(binder(), Mutator.class);
        mutatorMultibinder.addBinding().to(ThirdPartyMutatorTest.class);
    }

}