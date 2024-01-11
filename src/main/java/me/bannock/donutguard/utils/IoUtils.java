package me.bannock.donutguard.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IoUtils {

    /**
     * Reads all bytes from an input stream
     * @param in The input stream to read from
     * @return The bytes read from the input stream
     * @throws IOException If an error occurs while reading from the input stream
     */
    public static byte[] readBytesFromInputStream(InputStream in) throws IOException {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            final int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int readBytes;
            while ((readBytes = in.read(buffer, 0, bufferSize)) != -1){
                baos.write(buffer, 0, readBytes);
            }
            return baos.toByteArray();
        }
    }

}
