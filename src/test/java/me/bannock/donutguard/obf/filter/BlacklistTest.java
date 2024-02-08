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
import me.bannock.donutguard.utils.ResourceUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BlacklistTest {

    @Test
    void blacklistTest() throws IOException {
        Injector injector = Guice.createInjector(new ObfuscatorModule());

        File tempJar = File.createTempFile("Donutguard", "tmp.jar");
        tempJar.deleteOnExit();
        ResourceUtils.copyResourceToFile("test/testJars/Evaluator-1.0-SNAPSHOT.jar", tempJar);

        Configuration config = injector.getInstance(Configuration.class);
        DefaultConfigGroup.INPUT.set(config, tempJar);

        Obfuscator obfuscator = injector.getInstance(Obfuscator.class);
        ObfuscatorJobFactory jobFactory = injector.getInstance(ObfuscatorJobFactory.class);

        HashSet<JobStatus> endedStatuses = new HashSet<>(
                Arrays.asList(JobStatus.CANCELLED, JobStatus.COMPLETED, JobStatus.FAILED)
        );

        { // First test tests for single mutator blacklist
            DefaultConfigGroup.BLACKLIST.add(config, BlacklistMutatorTest.class.getName(),
                    "dev/sim0n/evaluator/Main\\.class");

            ObfuscatorJob job = jobFactory.create(config, new BlacklistMutatorModuleTest());
            obfuscator.submitJob(job);

            while (!endedStatuses.contains(obfuscator.getJobStatus(job)));

            // We remove all entries besides 1. We also have the single dummy entry.
            // So the value should be 2
            assertEquals(2, job.getJarHandler().getEntriesAmount());
        }

        { // Second test tests for global blacklist alongside mutator whitelist
            DefaultConfigGroup.BLACKLIST.clear(config, BlacklistMutatorModuleTest.class.getName());
            DefaultConfigGroup.BLACKLIST.add(config, null,
                    ".*");
            DefaultConfigGroup.WHITELIST.add(config, BlacklistMutatorTest.class.getName(),
                    "dev/sim0n/evaluator/Main\\.class");
            DefaultConfigGroup.WHITELIST.add(config, BlacklistMutatorTest.class.getName(),
                    "dev/sim0n/evaluator/Main\\$1\\.class");

            ObfuscatorJob job = jobFactory.create(config, new BlacklistMutatorModuleTest());

            obfuscator.submitJob(job);

            while (!endedStatuses.contains(obfuscator.getJobStatus(job)));

            // 32 in input jar + 1 dummy entry. Removing only two would result in 31
            assertEquals(31, job.getJarHandler().getEntriesAmount());
        }

        { // Third test tests for mutator blacklist alongside global whitelist
            DefaultConfigGroup.BLACKLIST.set(config, new HashMap<>());
            DefaultConfigGroup.WHITELIST.set(config, new HashMap<>());
            DefaultConfigGroup.BLACKLIST.add(config, BlacklistMutatorTest.class.getName(),
                    ".*");
            DefaultConfigGroup.WHITELIST.add(config, null,
                    "dev/sim0n/evaluator/Main\\.class");

            ObfuscatorJob job = jobFactory.create(config, new BlacklistMutatorModuleTest());

            obfuscator.submitJob(job);

            while (!endedStatuses.contains(obfuscator.getJobStatus(job)));

            // 32 in input jar + 1 dummy entry. Removing only one would result in 32
            assertEquals(32, job.getJarHandler().getEntriesAmount());
        }

        tempJar.delete();
    }

}
