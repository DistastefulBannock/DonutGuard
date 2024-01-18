package me.bannock.donutguard.logging;

import com.google.common.collect.ImmutableList;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

@Plugin(name = "DonutAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class Log4jDonutAppender extends AbstractAppender {

    private final List<LogEvent> logs = new ArrayList<>();
    private final HashSet<Consumer<LogEvent>> updateCallbacks = new HashSet<>();
    private boolean logging = true;

    @PluginFactory
    public static Log4jDonutAppender createDonutAppender(@PluginAttribute("name") String name,
                                                         @PluginElement("filter") Filter filter,
                                                         @PluginElement("Layout") Layout<?> layout){
        Log4jDonutAppender appender = new Log4jDonutAppender(name, filter, layout);
        DonutAppenderManager.manageNewAppender(appender);
        return appender;
    }

    @SuppressWarnings("deprecation")
    public Log4jDonutAppender(String name, Filter filter, Layout<?> layout) {
        super(name, filter, layout);
    }

    @Override
    public void append(LogEvent event) {
        // Log4j2 reuses the same event object but changes values, so we create a copy
        final LogEvent immutableEvent = event.toImmutable();

        if (logging)
            logs.add(immutableEvent);
        updateCallbacks.forEach(c -> c.accept(immutableEvent)); // Callback to event consumers
    }

    /**
     * Adds a new callback that is run when a new log event is passed into this appender
     * @param callback The callback to add
     */
    public void addCallbackConsumer(Consumer<LogEvent> callback){
        updateCallbacks.add(callback);
    }

    /**
     * Removes a new callback that is run when a new log event is passed into this appender
     * @param callback The callback to remove
     */
    public void removeCallbackConsumer(Consumer<LogEvent> callback){
        updateCallbacks.remove(callback);
    }

    /**
     * @return The logs passed into this appender
     */
    public ImmutableList<LogEvent> getLogs() {
        return ImmutableList.copyOf(logs);
    }

    /**
     * Clears the log history
     */
    public void clearLogHistory(){
        this.logs.clear();
    }

    /**
     * Sets this appender to no longer store new logs
     */
    public void stopLogging(){
        this.logging = false;
    }

}
