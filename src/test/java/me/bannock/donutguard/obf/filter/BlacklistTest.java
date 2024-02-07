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
import me.bannock.donutguard.obf.mutator.impl.NopSpammerMutator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

public class BlacklistTest {

    @Test
    void blacklistTest(){
        Injector injector = Guice.createInjector(new ObfuscatorModule());
        Obfuscator obfuscator = injector.getInstance(Obfuscator.class);
        ObfuscatorJobFactory jobFactory = injector.getInstance(ObfuscatorJobFactory.class);

        Configuration config = injector.getInstance(Configuration.class);
        DefaultConfigGroup.INPUT.set(config, new File("tools/Evaluator-1.0-SNAPSHOT.jar"));
        DefaultConfigGroup.NOP_SPAM_ENABLED.set(config, true);
        DefaultConfigGroup.DEV_TEST_MUTATOR_ENABLED.set(config, true);
        DefaultConfigGroup.BLACKLIST.add(config, null, "dev/sim0n/evaluator/Main.class");
        DefaultConfigGroup.WHITELIST.add(config, NopSpammerMutator.class.getName(), "dev/sim0n/evaluator/Main.class");

        ObfuscatorJob job = jobFactory.create(config, new ThirdPartyPluginModuleTest());
        obfuscator.submitJob(job);
        HashSet<JobStatus> endedStatuses = new HashSet<>(
                Arrays.asList(JobStatus.CANCELLED, JobStatus.COMPLETED, JobStatus.FAILED)
        );
        while (!endedStatuses.contains(obfuscator.getJobStatus(job)));
    }

}
