package me.bannock.donutguard;

import com.google.inject.Guice;
import com.google.inject.Injector;
import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.obf.CustomObfuscatorModule;
import me.bannock.donutguard.obf.Obfuscator;
import me.bannock.donutguard.obf.job.JobStatus;
import me.bannock.donutguard.obf.job.ObfuscatorJob;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

public class DonutGuardTest {

    @Test
    void quickStartExample(){
        // This will create the default config.
        // You may also load one from a file by calling ObfJobUtils.loadConfig
        ConfigDTO config = new ConfigDTO();
        config.input = new File("tools/Evaluator-1.0-SNAPSHOT.jar");
        config.output = new File("output1.jar");

        // A simple module will just store and use the passed in config.
//        Injector injector = Guice.createInjector(new SimpleObfuscatorModule(configDTO));

        // You could also use a CustomObfuscatorModule if you wish to
        // provide the config from a field, variable, or with guice.
        // In the example below, we create a new provider and pass it into
        // a custom module. Since the provider is called every time it needs
        // to be injected, you could add logic or return from a changing field.
        // You may get a provider from guice as well, check out
        // me.bannock.donutguard.DonutGuardModule for an example of this.
        Injector injector = Guice.createInjector(new CustomObfuscatorModule(() -> config));

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
        // both jobs are completed
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

}
