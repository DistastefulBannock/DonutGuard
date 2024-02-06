package me.bannock.donutguard.obf.plugin;

import com.google.inject.Guice;
import com.google.inject.Injector;
import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.obf.Obfuscator;
import me.bannock.donutguard.obf.ObfuscatorModule;
import me.bannock.donutguard.obf.job.JobStatus;
import me.bannock.donutguard.obf.job.ObfuscatorJob;
import me.bannock.donutguard.obf.job.ObfuscatorJobFactory;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

public class ObfuscatorPluginTest {

    @Test
    void usePlugin(){
        Injector injector = Guice.createInjector(new ObfuscatorModule());
        Obfuscator obfuscator = injector.getInstance(Obfuscator.class);
        ObfuscatorJobFactory jobFactory = injector.getInstance(ObfuscatorJobFactory.class);
        ConfigDTO config = new ConfigDTO();
        config.input = new File("tools/Evaluator-1.0-SNAPSHOT.jar");
        ObfuscatorJob job = jobFactory.create(config, new ThirdPartyPluginTest());
        obfuscator.submitJob(job);
        HashSet<JobStatus> endedStatuses = new HashSet<>(
                Arrays.asList(JobStatus.CANCELLED, JobStatus.COMPLETED, JobStatus.FAILED)
        );
        while (!endedStatuses.contains(obfuscator.getJobStatus(job)));
    }

}
