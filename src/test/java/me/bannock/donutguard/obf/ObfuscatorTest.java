package me.bannock.donutguard.obf;

import com.google.inject.Guice;
import com.google.inject.Injector;
import me.bannock.donutguard.obf.job.JobStatus;
import me.bannock.donutguard.obf.job.ObfuscatorJob;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class ObfuscatorTest {

    @Test
    void quickStartExample(){
        // This will create the default config.
        // You may also load one from a file by calling ObfJobUtils.loadConfig
        ConfigDTO config = new ConfigDTO();
        config.input = new File("tools/Evaluator-1.0-SNAPSHOT.jar");
        config.output = new File("output1.jar");

        // This module constructor just stores and uses the passed in config instance.
        // You are unable to change the object reference used by created jobs
        // when using this module.
        Injector injector = Guice.createInjector(new ObfuscatorModule(config));

        // You could also use a provider if you wish to
        // provide the config from a field, variable, or with guice.
        // In the example below, we create a new provider and pass it into
        // the module. Since the provider is called every time it needs
        // to be injected, you could add logic or return from a changing field.
        // You may get a provider from guice as well, check out
        // me.bannock.donutguard.DonutGuardModule for an example of this.
//        Injector injector = Guice.createInjector(new ObfuscatorModule(() -> config));

        // Create the Obfuscator instance with guice
        Obfuscator obfuscator = injector.getInstance(Obfuscator.class);

        // This will create a job with the configuration passed in with the module.
        // The job will clone the config on creation to prevent you from accidentally
        // changing config values after creating the job.
        ObfuscatorJob job1 = injector.getInstance(ObfuscatorJob.class);

        // In this example we change the config and create a second job to show the
        // multithreading capabilities.
        config.nopSpammerEnabled = true;
        config.output = new File("output2.jar");
        ObfuscatorJob job2 = injector.getInstance(ObfuscatorJob.class);

        // We then tell the obfuscator to queue the jobs. They will soon be ran.
        obfuscator.submitJob(job1);
        obfuscator.submitJob(job2);

        // The jobs will run on different threads, so we will do a simple busy sleep until
        // both jobs are completed. Ideally your program should continue to run without
        // something like this, but since we are removing the jobs below, we need to wait
        // for them to complete.
        HashSet<JobStatus> endedStatuses = new HashSet<>(
                Arrays.asList(JobStatus.CANCELLED, JobStatus.COMPLETED, JobStatus.FAILED)
        );
        while (!endedStatuses.contains(obfuscator.getJobStatus(job1))
                || !endedStatuses.contains(obfuscator.getJobStatus(job2)));

        // The output files should now be written and the jobs should be completed.
        // However, the entries from the jars are still loaded in memory and will stay
        // there until the gc removes them, but this will not happen until the jobs are
        // removed
        obfuscator.removeJob(job1);
        obfuscator.removeJob(job2);

    }

    @Test
    void jobNotFoundTest(){
        ConfigDTO config = new ConfigDTO();
        config.input = new File("tools/Evaluator-1.0-SNAPSHOT.jar");
        Injector injector = Guice.createInjector(new ObfuscatorModule(config));
        Obfuscator obfuscator1 = injector.getInstance(Obfuscator.class);
        Obfuscator obfuscator2 = injector.getInstance(Obfuscator.class);
        ObfuscatorJob job1 = injector.getInstance(ObfuscatorJob.class);
        ObfuscatorJob job2 = injector.getInstance(ObfuscatorJob.class);
        assertSame(obfuscator1.getJobStatus(job1), JobStatus.NOT_FOUND);
        obfuscator1.submitJob(job1);
        assertNotSame(obfuscator1.getJobStatus(job1), JobStatus.NOT_FOUND);
        assertSame(obfuscator2.getJobStatus(job1), JobStatus.NOT_FOUND);
        assertSame(obfuscator1.getJobStatus(job2), JobStatus.NOT_FOUND);
    }

}
