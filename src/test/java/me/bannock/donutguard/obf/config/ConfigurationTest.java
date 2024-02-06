package me.bannock.donutguard.obf.config;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import me.bannock.donutguard.obf.ObfuscatorModule;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationTest {

    @Test
    void testDonutGuardConfigGroup(){
        Injector injector = Guice.createInjector(new ObfuscatorModule());
        Configuration config = injector.getInstance(Configuration.class);
        assertTrue(DonutGuardConfigGroup.DEV_TEST_MUTATOR_ENABLED.get(config));
        DonutGuardConfigGroup.DEV_TEST_MUTATOR_ENABLED.set(config, false);
        assertFalse(DonutGuardConfigGroup.DEV_TEST_MUTATOR_ENABLED.get(config));
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

}