package me.bannock.donutguard.ui.logs.models;

import java.util.List;
import java.util.function.Consumer;

public interface LogsModel {

    /**
     * Starts a thread to monitor the desired logs
     */
    void startMonitor();

    /**
     * Stops the thread that is monitoring the logs. Needed unless you want a resource leak
     */
    void stopMonitor();

    /**
     * Gets all the logs entries and returns them in a list
     * @return A list of all the logs
     */
    List<String> getAllLogs();

    /**
     * Clears all log entries
     */
    void clearLogs();

    /**
     * Adds a consumer that will receive new logs upon creation
     * @param consumer The consumer that will receive the logs
     */
    void addLogConsumer(Consumer<String> consumer);

    /**
     * Removes a log consumer so that it will no longer receive logs
     * @param consumer The consumer that is currently consuming logs
     */
    void removeLogConsumer(Consumer<String> consumer);

}
