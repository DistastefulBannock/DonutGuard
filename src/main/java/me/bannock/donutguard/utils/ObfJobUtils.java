package me.bannock.donutguard.utils;

import com.google.inject.Guice;
import com.google.inject.Injector;
import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.obf.DummyObfuscatorModule;
import me.bannock.donutguard.obf.Obfuscator;
import me.bannock.donutguard.obf.job.ObfuscatorJob;
import org.apache.commons.lang3.SerializationUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.AbstractMap;
import java.util.Map;

public class ObfJobUtils {

    /**
     * @param jobName The job name to get the log file for
     * @return The log file for the given job name
     */
    public static File getLogFileLocation(String jobName){
        return new File("logs/DonutGuard " + jobName + ".log");
    }

    /**
     * Quick utility method to create and start a new job with the given config.
     * Definitely not the best way to create jobs but still usable.
     * @param config The config the job should use
     * @return An entry containing the created obfuscator and job instances
     */
    public static Map.Entry<Obfuscator, ObfuscatorJob> quickStartJob(ConfigDTO config){
        Obfuscator obfuscator = quickCreateObfuscator();
        ObfuscatorJob job = quickCreateJob(config);
        obfuscator.submitJob(job);
        return new AbstractMap.SimpleEntry<>(obfuscator, job);
    }

    /**
     * Creates a new obfuscator instance
     * @return The obfuscator instance
     */
    public static Obfuscator quickCreateObfuscator(){
        return Guice.createInjector().getInstance(Obfuscator.class);
    }

    /**
     * Creates a new obfuscator job instance
     * @param config The config that the job should use
     * @return The obfuscator job instance
     */
    public static ObfuscatorJob quickCreateJob(ConfigDTO config){
        Injector injector = Guice.createInjector(new DummyObfuscatorModule(config));
        return injector.getInstance(ObfuscatorJob.class);
    }

    /**
     * Loads a config with its bytes
     * @param bytes The bytes of the config
     * @return The config object
     */
    public static ConfigDTO loadConfig(byte[] bytes){
        return SerializationUtils.deserialize(bytes);
    }

    /**
     * Loads a config from a file
     * @param file The file to load the config from
     * @return The config object
     * @throws IOException If something went wrong while reading the file
     */
    public static ConfigDTO loadConfig(File file) throws IOException{
        return loadConfig(Files.readAllBytes(file.toPath()));
    }

    /**
     * Serializes a config object into its bytes
     * @param config The config instance
     * @return The serialized bytes of the config
     */
    public static byte[] getConfigBytes(ConfigDTO config){
        return SerializationUtils.serialize(config);
    }

}
