package me.bannock.donutguard.obf.config.thirdparty;

import com.google.inject.Guice;
import com.google.inject.Injector;
import me.bannock.donutguard.obf.ObfuscatorModule;
import me.bannock.donutguard.obf.config.Configuration;
import me.bannock.donutguard.obf.mutator.cfg.TestingCfgGroup;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ThirdPartyConfigTest {

    @Test
    void testDonutGuardConfigGroup(){
        Injector injector = Guice.createInjector(new ObfuscatorModule(), new ThirdPartyConfigModuleTest());
        Configuration config = injector.getInstance(Configuration.class);
        assertFalse(TestingCfgGroup.DEV_TEST_MUTATOR_ENABLED.get(config));
        TestingCfgGroup.DEV_TEST_MUTATOR_ENABLED.set(config, true);
        assertTrue(TestingCfgGroup.DEV_TEST_MUTATOR_ENABLED.get(config));

        assertTrue(ThirdPartyConfigGroupTest.THIRD_PARTY_MUTATOR_ON.get(config));
        ThirdPartyConfigGroupTest.THIRD_PARTY_MUTATOR_ON.set(config, false);
        assertFalse(ThirdPartyConfigGroupTest.THIRD_PARTY_MUTATOR_ON.get(config));
    }
    
}
