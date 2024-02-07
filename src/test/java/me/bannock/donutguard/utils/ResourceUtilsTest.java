package me.bannock.donutguard.utils;

import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class ResourceUtilsTest {

    @Test
    void readBytes() {
        ThreadContext.put("threadId", "ResourceUtilsTest");
        byte[] bytes = ResourceUtils.readBytes("test/testResources/Java Class Magic.bin");
        assertArrayEquals(bytes, new byte[]{-54, -2, -70, -66});
    }

    @Test
    void readString() {
        ThreadContext.put("threadId", "ResourceUtilsTest");
        String str = ResourceUtils.readString("test/testResources/Donutguard.txt");
        assertEquals(str, "Donutguard");
    }

    @Test
    void copyResource() throws IOException {
        ThreadContext.put("threadId", "ResourceUtilsTest");
        File tempFile = File.createTempFile("Donutguard", "tmp");
        try{
            ResourceUtils.copyResourceToFile("test/testResources/Java Class Magic.bin", tempFile);
            assertTrue(tempFile.exists());
            byte[] tempFileBytes = Files.readAllBytes(tempFile.toPath());
            assertArrayEquals(tempFileBytes, new byte[]{-54, -2, -70, -66});
            tempFile.delete();
        }catch (Throwable e){
            tempFile.delete();
            throw e;
        }
    }

}