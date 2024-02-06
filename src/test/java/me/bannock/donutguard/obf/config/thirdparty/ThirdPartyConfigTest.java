package me.bannock.donutguard.obf.config.thirdparty;

import com.google.inject.Guice;
import com.google.inject.Injector;
import me.bannock.donutguard.obf.ObfuscatorModule;
import me.bannock.donutguard.obf.config.Configuration;
import me.bannock.donutguard.obf.config.DonutGuardConfigGroup;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ThirdPartyConfigTest {

    @Test
    void testDonutGuardConfigGroup(){
        Injector injector = Guice.createInjector(new ObfuscatorModule(), new ThirdPartyConfigModuleTest());
        Configuration config = injector.getInstance(Configuration.class);
        assertTrue(DonutGuardConfigGroup.DEV_TEST_MUTATOR_ENABLED.get(config));
        DonutGuardConfigGroup.DEV_TEST_MUTATOR_ENABLED.set(config, false);
        assertFalse(DonutGuardConfigGroup.DEV_TEST_MUTATOR_ENABLED.get(config));

        assertTrue(ThirdPartyConfigGroupTest.THIRD_PARTY_MUTATOR_ON.get(config));
        ThirdPartyConfigGroupTest.THIRD_PARTY_MUTATOR_ON.set(config, false);
        assertFalse(ThirdPartyConfigGroupTest.THIRD_PARTY_MUTATOR_ON.get(config));
    }

}
