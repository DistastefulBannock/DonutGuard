package me.bannock.donutguard.utils;

import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.obf.Obfuscator;
import me.bannock.donutguard.obf.job.ObfuscatorJob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ObfJobUtilsTest {

    static{
        ThreadContext.put("threadId", "Testing");
    }
    private static final Logger logger = LogManager.getLogger();

    @Test
    void quickStartJob() {
        ConfigDTO config = new ConfigDTO();
        config.nopSpammerEnabled = true;
        config.computeMaxes = false;
        config.computeFrames = false;
        config.input = new File("target/DonutGuard-1.0-SNAPSHOT.jar");
        Map.Entry<Obfuscator, ObfuscatorJob> result = ObfJobUtils.quickStartJob(config);
        whileLoop:
        while (true){
            switch (result.getKey().getJobStatus(result.getValue())){
                case FAILED:
                case COMPLETED:
                case CANCELLED:
                case NOT_FOUND:
                    break whileLoop;
            }
        }
    }

    @Test
    void quickCreateObfuscator() {
        Obfuscator obfuscator = ObfJobUtils.quickCreateObfuscator();
        assertNotNull(obfuscator);
        assertNotNull(obfuscator.getJobs());
    }

    @Test
    void quickCreateJob() {
        ObfuscatorJob job = ObfJobUtils.quickCreateJob(new ConfigDTO());
        assertNotNull(job.getThreadId());
        assertNotNull(job.getJarHandler());
    }

    @Test
    void loadConfigResource() {
        ConfigDTO config = ObfJobUtils.loadConfig(
                ResourceUtils.readBytes("obfUtils/loadConfigFromResource.bin")
        );
        assertTrue(config.nopSpammerEnabled); // This config has it enabled
    }

    @Test
    void writeAndLoadConfigFile() throws IOException{
        ConfigDTO config = new ConfigDTO();
        config.nopSpammerEnabled = true;
        File tmpConfigFile = File.createTempFile("config", ".bin");
        logger.info(String.format("Writing test config to \"%s\"", tmpConfigFile.getAbsolutePath()));
        tmpConfigFile.deleteOnExit();
        Files.write(tmpConfigFile.toPath(), ObfJobUtils.getConfigBytes(config));
        assertTrue(tmpConfigFile.exists());
        logger.info("Loading test config and checking for matched value");
        ConfigDTO configFromFile = ObfJobUtils.loadConfig(tmpConfigFile);
        assertTrue(configFromFile.nopSpammerEnabled);
        tmpConfigFile.delete();
    }

}