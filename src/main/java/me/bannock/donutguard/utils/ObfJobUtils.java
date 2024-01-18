package me.bannock.donutguard.utils;

import java.io.File;

public class ObfJobUtils {

    /**
     * @param jobName The job name to get the log file for
     * @return The log file for the given job name
     */
    public static File getLogFileLocation(String jobName){
        return new File("logs/DonutGuard " + jobName + ".log");
    }

}
