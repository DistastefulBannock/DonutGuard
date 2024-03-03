package me.bannock.donutguard.obf.mutator.impl.string;

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
import java.util.HashSet;

class LineNumberStringLiteralMutatorTest {

    @Test
    void testStringEncryption() throws IOException {
        Injector injector = Guice.createInjector(new ObfuscatorModule());

        Configuration config = injector.getInstance(Configuration.class);
        StringEncConfigGroup.STRING_ENC_ENABLED.set(config, true);
        StringEncConfigGroup.STRING_ENC_TYPE.set(config,
                StringLiteralEncryptionType.IDENTIFIERS_VIA_LINE_NUMBERS_AND_INTS);
//        DefaultConfigGroup.COMPUTE_FRAMES.set(config, true);

        File tempJar = File.createTempFile("Donutguard", "tmp.jar");
        tempJar.deleteOnExit();
        ResourceUtils.copyResourceToFile("test/testJars/Evaluator-1.0-SNAPSHOT.jar", tempJar);
        DefaultConfigGroup.INPUT.set(config, tempJar);

        Obfuscator obfuscator = injector.getInstance(Obfuscator.class);
        ObfuscatorJobFactory jobFactory = injector.getInstance(ObfuscatorJobFactory.class);
        ObfuscatorJob job = jobFactory.create(config);
        obfuscator.submitJob(job);

        HashSet<JobStatus> endedStatuses = new HashSet<>(
                Arrays.asList(JobStatus.CANCELLED, JobStatus.COMPLETED, JobStatus.FAILED));
        while (!endedStatuses.contains(obfuscator.getJobStatus(job)));

        tempJar.delete();
    }

}