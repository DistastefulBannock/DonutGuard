package me.bannock.donutguard.obf.config;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import me.bannock.donutguard.obf.ObfuscatorModule;
import me.bannock.donutguard.utils.ConfigurationUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationTest {

    @Test
    void testDonutGuardConfigGroup(){
        Injector injector = Guice.createInjector(new ObfuscatorModule());
        Configuration config = injector.getInstance(Configuration.class);
        assertFalse(DefaultConfigGroup.DEV_TEST_MUTATOR_ENABLED.get(config));
        DefaultConfigGroup.DEV_TEST_MUTATOR_ENABLED.set(config, true);
        assertTrue(DefaultConfigGroup.DEV_TEST_MUTATOR_ENABLED.get(config));
    }

    @Test
    void makeSureGuiceIsDoingWhatIWantItToDo(){
        Injector injector = Guice.createInjector(new ObfuscatorModule());
        injector.getInstance(WeirdTesting.class);
    }

    public static class WeirdTesting{
        @Inject
        public WeirdTesting(Set<ConfigurationGroup> groups1, Set<ConfigurationGroup> groups2){
            assertNotSame(groups1, groups2);
        }
    }

    @Test
    void configurationCloningTest(){
        Injector injector = Guice.createInjector(new ObfuscatorModule());
        Configuration configuration1 = injector.getInstance(Configuration.class);
        Configuration configuration2 = SerializationUtils.clone(configuration1);
        assertNotSame(configuration1, configuration2);
    }

    @Test
    void configurationSaveLoadTest(){
        Injector injector = Guice.createInjector(new ObfuscatorModule());
        Configuration configuration1 = injector.getInstance(Configuration.class);
        DefaultConfigGroup.NOP_SPAM_ENABLED.set(configuration1, true);
        byte[] bytes = ConfigurationUtils.getConfigBytes(configuration1);
        Configuration configuration2 = ConfigurationUtils.loadConfig(bytes);
        assertNotSame(configuration1, configuration2);
        assertSame(DefaultConfigGroup.NOP_SPAM_ENABLED.get(configuration1),
                DefaultConfigGroup.NOP_SPAM_ENABLED.get(configuration2));
    }

}