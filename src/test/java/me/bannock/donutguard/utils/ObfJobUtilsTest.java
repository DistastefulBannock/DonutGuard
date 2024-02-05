package me.bannock.donutguard.utils;

import me.bannock.donutguard.obf.ConfigDTO;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class ObfJobUtilsTest {

    static{
        ThreadContext.put("threadId", "Testing");
    }
    private static final Logger logger = LogManager.getLogger();

    @Test
    void loadConfigResource() {
        ConfigDTO config = ObfJobUtils.loadConfig(
                ResourceUtils.readBytes("obfUtils/loadConfigFromResource.json")
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

    @Test
    void makeSureConfigLoadingDoesntUseNullValuesWhenMissingInConfigFile(){
        ConfigDTO config1 = new ConfigDTO();
        ConfigDTO config2 = ObfJobUtils.loadConfig(
                ResourceUtils.readBytes("obfUtils/empty.json")
        );
        assertArrayEquals(SerializationUtils.serialize(config1),
                SerializationUtils.serialize(config2));
    }

}