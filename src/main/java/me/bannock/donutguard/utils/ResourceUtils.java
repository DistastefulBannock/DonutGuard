package me.bannock.donutguard.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResourceUtils {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Reads the bytes from a specific resource
     * @param resourcePath The path to the resource
     * @return The bytes from the resource
     */
    public static byte[] readBytes(String resourcePath){
        logger.info("Attempting to read the bytes from resource \"" + resourcePath + "\"...");
        try (InputStream in = ResourceUtils.class.getClassLoader().getResourceAsStream(resourcePath);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            if (in == null){
                logger.warn("Resource input stream is null; see below:");
                throw new NullPointerException("Resource input stream is null");
            }
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1){
                baos.write(buffer, 0, read);
            }
            logger.info("Successfully read the bytes from resource \"" + resourcePath + "\"");
            return baos.toByteArray();
        } catch (IOException | NullPointerException e){
            logger.error("Failed to read the bytes from resource \"" + resourcePath + "\"", e);
            return "Resource not found".getBytes();
        }
    }

    /**
     * Reads the string from a specific resource
     * @param resourcePath The path to the resource
     * @return The string from the resource
     */
    public static String readString(String resourcePath){
        return new String(readBytes(resourcePath));
    }

    /**
     * Copys a resource to a file
     * @param resourcePath The resource path to load the bytes from
     * @param path The path to write the bytes to
     * @throws IOException If something goes wrong while writing the file
     */
    public static void copyResourceToFile(String resourcePath, Path path) throws IOException {
        Files.write(path, readBytes(resourcePath));
    }

    /**
     * Copys a resource to a file
     * @param resourcePath The resource path to load the bytes from
     * @param file The file to write the bytes to
     * @throws IOException If something goes wrong while writing the file
     */
    public static void copyResourceToFile(String resourcePath, File file) throws IOException {
        copyResourceToFile(resourcePath, file.toPath());
    }

}
