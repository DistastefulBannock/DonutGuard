package me.bannock.donutguard.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class DonutAppenderManager {

    private static final Logger logger = LogManager.getLogger();
    private static final Map<String, Log4jDonutAppender> appenders = new HashMap<>();

    /**
     * Called on creation of a new donut appender
     * @param appender The appender instance to manage
     */
    protected static void manageNewAppender(Log4jDonutAppender appender){
        appenders.put(appender.getName(), appender);
    }

    /**
     * Returns an appender of a specific name
     * @param name The name of the appender
     * @return The instance of the appender, or null if none is found
     */
    public static Log4jDonutAppender getAppender(String name){
        return appenders.get(name);
    }

    /**
     * Clears the log history of and stops managing one appender
     * @param name The name of the appender we should stop managing
     */
    public static void stopManagingAppender(String name){
        logger.info("Stopping management of appender \"" + name + "\"...");
        Log4jDonutAppender appender = getAppender(name);
        if (appender == null) {
            logger.warn("Appender with name \"" + name + "\" not found");
            return;
        }
        appender.stopLogging();
        appender.clearLogHistory();
        logger.info(String.format(
                "Stopped logging and cleared log history for appender \"%s\"", name));
        logger.info(String.format("Successfully stopped managing appender \"%s\"", name));
    }

}
