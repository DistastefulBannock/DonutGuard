package me.bannock.donutguard.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.bannock.donutguard.obf.config.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ObfJobUtils {

    /**
     * @param jobName The job name to get the log file for
     * @return The log file for the given job name
     */
    public static File getLogFileLocation(String jobName){
        return new File("logs/DonutGuard " + jobName + ".log");
    }

    /**
     * Loads a config with its bytes
     * @param bytes The bytes of the config
     * @return The config object
     */
    public static Configuration loadConfig(byte[] bytes){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(new String(bytes, StandardCharsets.UTF_8), Configuration.class);
    }

    /**
     * Loads a config from a file
     * @param file The file to load the config from
     * @return The config object
     * @throws IOException If something went wrong while reading the file
     */
    public static Configuration loadConfig(File file) throws IOException{
        return loadConfig(Files.readAllBytes(file.toPath()));
    }

    /**
     * Serializes a config object into its bytes
     * @param config The config instance
     * @return The serialized bytes of the config
     */
    public static byte[] getConfigBytes(Configuration config){
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        return gson.toJson(config).getBytes(StandardCharsets.UTF_8);
    }

}
