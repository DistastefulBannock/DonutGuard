package me.bannock.donutguard.obf.filter;

import com.google.inject.Guice;
import com.google.inject.Injector;
import me.bannock.donutguard.obf.Obfuscator;
import me.bannock.donutguard.obf.ObfuscatorModule;
import me.bannock.donutguard.obf.config.Configuration;
import me.bannock.donutguard.obf.config.DefaultConfigGroup;
import me.bannock.donutguard.obf.job.JobStatus;
import me.bannock.donutguard.obf.job.ObfuscatorJob;
import me.bannock.donutguard.obf.job.ObfuscatorJobFactory;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class BlacklistTest {

    @Test
    void blacklistTest(){
        Injector injector = Guice.createInjector(new ObfuscatorModule());
        Obfuscator obfuscator = injector.getInstance(Obfuscator.class);
        ObfuscatorJobFactory jobFactory = injector.getInstance(ObfuscatorJobFactory.class);

        Configuration config = injector.getInstance(Configuration.class);
        DefaultConfigGroup.INPUT.set(config, new File("tools/Evaluator-1.0-SNAPSHOT.jar"));

        ObfuscatorJob job = jobFactory.create(config, new ThirdPartyPluginModuleTest());
        obfuscator.submitJob(job);
        HashSet<JobStatus> endedStatuses = new HashSet<>(
                Arrays.asList(JobStatus.CANCELLED, JobStatus.COMPLETED, JobStatus.FAILED)
        );
        while (!endedStatuses.contains(obfuscator.getJobStatus(job)));
        // The third party mutator sets this value to true in their config instance.
        // Checking to ensure that there are no references shared as the mutators should be using
        // a deep clone of the config
        assertFalse(DefaultConfigGroup.NOP_SPAM_ENABLED.get(config));
    }

}
