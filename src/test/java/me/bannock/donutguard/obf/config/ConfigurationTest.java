package me.bannock.donutguard.obf.config;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import me.bannock.donutguard.obf.ObfuscatorModule;
import me.bannock.donutguard.obf.mutator.cfg.NopSpamCfgGroup;
import me.bannock.donutguard.obf.mutator.cfg.TestingCfgGroup;
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
        assertFalse(TestingCfgGroup.DEV_TEST_MUTATOR_ENABLED.getBool(config));
        TestingCfgGroup.DEV_TEST_MUTATOR_ENABLED.setBool(config, true);
        assertTrue(TestingCfgGroup.DEV_TEST_MUTATOR_ENABLED.getBool(config));
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
        assertNotSame(DefaultConfigGroup.INPUT.getFile(configuration1),
                DefaultConfigGroup.INPUT.getFile(configuration2));
        assertNotSame(DefaultConfigGroup.BLACKLIST.getObj(configuration1).getMappings(),
                DefaultConfigGroup.BLACKLIST.getObj(configuration2).getMappings());
    }

    @Test
    void configurationSaveLoadTest(){
        Injector injector = Guice.createInjector(new ObfuscatorModule());
        Configuration configuration1 = injector.getInstance(Configuration.class);
        NopSpamCfgGroup.NOP_SPAM_ENABLED.setBool(configuration1, true);
        byte[] bytes = ConfigurationUtils.getConfigBytes(configuration1);
        Configuration configuration2 = ConfigurationUtils.loadConfig(bytes);
        assertNotSame(configuration1, configuration2);
        assertSame(NopSpamCfgGroup.NOP_SPAM_ENABLED.getBool(configuration1),
                NopSpamCfgGroup.NOP_SPAM_ENABLED.getBool(configuration2));
    }

}