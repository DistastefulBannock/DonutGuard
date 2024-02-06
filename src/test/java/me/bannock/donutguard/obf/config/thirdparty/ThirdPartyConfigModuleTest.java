package me.bannock.donutguard.obf.config.thirdparty;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import me.bannock.donutguard.obf.config.ConfigurationGroup;

public class ThirdPartyConfigModuleTest extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder<ConfigurationGroup> configurationGroupMultibinder =
                Multibinder.newSetBinder(binder(), ConfigurationGroup.class);
        configurationGroupMultibinder.addBinding().to(ThirdPartyConfigGroupTest.class);
    }
}
