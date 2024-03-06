package me.bannock.donutguard.obf;

import com.google.inject.Guice;
import com.google.inject.Injector;
import me.bannock.donutguard.obf.config.Configuration;
import me.bannock.donutguard.obf.config.DefaultConfigGroup;
import me.bannock.donutguard.obf.job.JobStatus;
import me.bannock.donutguard.obf.job.ObfuscatorJob;
import me.bannock.donutguard.obf.job.ObfuscatorJobFactory;
import me.bannock.donutguard.obf.mutator.cfg.NopSpamCfgGroup;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

public class ObfuscatorTest {

    @Test
    void quickStartExample(){

        // This module constructor just stores and uses the passed in config instance.
        // You are unable to change the object reference used by created jobs
        // when using this module.
        Injector injector = Guice.createInjector(new ObfuscatorModule());

        // This will create the default config.
        // You may also load one from a file by calling ObfJobUtils.loadConfig
        Configuration config = injector.getInstance(Configuration.class);
        DefaultConfigGroup.INPUT.setFile(config, new File("tools/Evaluator-1.0-SNAPSHOT.jar"));
        DefaultConfigGroup.OUTPUT.setFile(config, new File("output1.jar"));


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

        // We then create a job factory. We need a factory so we can use guice's assisted injection
        ObfuscatorJobFactory jobFactory = injector.getInstance(ObfuscatorJobFactory.class);

        // This will create a job with the configuration passed in with the module.
        // The job will clone the config on creation to prevent you from accidentally
        // changing config values after creating the job.
        ObfuscatorJob job1 = jobFactory.create(config);

        // In this example we change the config and create a second job to show the
        // multithreading capabilities.
        NopSpamCfgGroup.NOP_SPAM_ENABLED.setBool(config, true);
        DefaultConfigGroup.OUTPUT.setFile(config, new File("output2.jar"));
        ObfuscatorJob job2 = jobFactory.create(config);

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
        Injector injector = Guice.createInjector(new ObfuscatorModule());
        Configuration config = injector.getInstance(Configuration.class);
        DefaultConfigGroup.INPUT.setFile(config, new File("tools/Evaluator-1.0-SNAPSHOT.jar"));
        Obfuscator obfuscator1 = injector.getInstance(Obfuscator.class);
        Obfuscator obfuscator2 = injector.getInstance(Obfuscator.class);
        ObfuscatorJobFactory jobFactory = injector.getInstance(ObfuscatorJobFactory.class);
        ObfuscatorJob job1 = jobFactory.create(config);
        ObfuscatorJob job2 = jobFactory.create(config);
        assertSame(obfuscator1.getJobStatus(job1), JobStatus.NOT_FOUND);
        obfuscator1.submitJob(job1);
        assertNotSame(obfuscator1.getJobStatus(job1), JobStatus.NOT_FOUND);
        assertSame(obfuscator2.getJobStatus(job1), JobStatus.NOT_FOUND);
        assertSame(obfuscator1.getJobStatus(job2), JobStatus.NOT_FOUND);
    }

    @Test
    void whyIsThisHappeningToMe(){
        Injector injector = Guice.createInjector(new ObfuscatorModule());

        // This loads config groups
        Configuration configuration = injector.getInstance(Configuration.class);

        // This also loads config groups? What the fuck
        ObfuscatorJobFactory jobFactory = injector.getInstance(ObfuscatorJobFactory.class);
        ObfuscatorJob job = jobFactory.create(configuration);

    }

}
