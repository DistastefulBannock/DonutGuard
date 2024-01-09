package me.bannock.donutguard.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class HwidUtil {

    public static int getHwid() {
        // Get information about computer and generate a hash to use as a unique identifier
        String os = System.getProperty("os.name");
        String user = System.getProperty("user.name");
        String arch = System.getProperty("os.arch");
        String cpuCount = Runtime.getRuntime().availableProcessors() + "";
        StringBuilder drivePaths = new StringBuilder();
        for (File drive : File.listRoots())
            drivePaths.append(drive.getAbsolutePath());
        StringBuilder gpu = new StringBuilder();
        try{
            String line;
            Process p = Runtime.getRuntime().exec("wmic PATH Win32_videocontroller GET description");
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                gpu.append(line);
            }
            input.close();
        }catch (Exception ignored){}

        String hwid = os + "-" + user + "-" + arch + "-" + cpuCount + "-" + drivePaths + "-" + gpu;
        hwid = hwid.strip().replaceAll("\\\\s+", "").replaceAll("[\n\r\t]", "");
        return hwid.hashCode();
    }

}
