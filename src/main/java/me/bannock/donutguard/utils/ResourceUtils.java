package me.bannock.donutguard.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResourceUtils {

    /**
     * Reads the bytes from a specific resource
     * @param resourcePath The path to the resource
     * @return The bytes from the resource
     */
    public static byte[] readBytes(String resourcePath){
        try (InputStream in = ResourceUtils.class.getClassLoader().getResourceAsStream(resourcePath);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            if (in == null)
                throw new NullPointerException("Resource input stream is null");
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1){
                baos.write(buffer, 0, read);
            }
            return baos.toByteArray();
        } catch (IOException | NullPointerException e){
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

}
