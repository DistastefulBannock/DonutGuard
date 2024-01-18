package me.bannock.donutguard.ui.logs;

import me.bannock.donutguard.logging.DonutAppenderManager;
import me.bannock.donutguard.logging.Log4jDonutAppender;
import me.bannock.donutguard.utils.ObfJobUtils;
import me.bannock.donutguard.utils.UiUtils;
import org.apache.logging.log4j.core.LogEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.function.Consumer;

public class LogsFrame extends JFrame implements Consumer<LogEvent> {

    private final Log4jDonutAppender appender;
    private final JEditorPane logs;
    private final JCheckBox lockToBottom;

    public LogsFrame(String name) {
        this.appender = DonutAppenderManager.getAppender(name);
        if (this.appender == null)
            throw new IllegalArgumentException("No appender with name \"" +
                    name + "\" exists. Could not create log window.");

        // With our appender, we need to add a line to unsubscribe this
        // frame's callback on close so we don't accidentally create a memory leak
        final LogsFrame finalThis = this; // We need this so our window adapter can access this instance
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                appender.removeCallbackConsumer(finalThis);
            }
        });

        setSize(550, 300);
        setResizable(true);
        setTitle(name + " - logs");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.logs = new JEditorPane();

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BorderLayout());
        this.lockToBottom = new JCheckBox("Lock scrolling");
        lockToBottom.setSelected(true);

        JPanel extraButtonsContainer = new JPanel();
        extraButtonsContainer.setLayout(new BoxLayout(extraButtonsContainer, BoxLayout.X_AXIS));
        JButton openLogFile = new JButton("Open Log File");
        openLogFile.addActionListener(evt -> UiUtils.openFile(ObfJobUtils.getLogFileLocation(name)));
        JButton clearLogs = new JButton("Clear Logs");
        clearLogs.addActionListener(evt -> {
            appender.clearLogHistory();
            try {
                logs.getDocument().remove(0, logs.getDocument().getLength());
            } catch (BadLocationException ignored) {}
        });
        extraButtonsContainer.add(openLogFile);
        extraButtonsContainer.add(clearLogs);

        optionsPanel.add(lockToBottom, BorderLayout.WEST);
        optionsPanel.add(extraButtonsContainer, BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
            if (lockToBottom.isSelected())
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
        });

        logs.setEditable(false);
        logs.setBorder(null);
        logs.setBackground(null);
        scrollPane.setViewportView(logs);

        add(scrollPane, BorderLayout.CENTER);
        add(optionsPanel, BorderLayout.SOUTH);

        // We wait until the end to populate the logs as all the frame construction
        // has already been completed and anything coming in from the callback
        // will have access to the fully constructed frame and its components
        //
        // We only get 100 so the program won't hang
        List<LogEvent> top100MostRecentLogs = appender.getLogs();
        top100MostRecentLogs = top100MostRecentLogs.subList(
                top100MostRecentLogs.size() - 100, top100MostRecentLogs.size());
        Document document = logs.getDocument();
        for (LogEvent log : top100MostRecentLogs){
            try {
                document.insertString(document.getLength(), formatLogEvent(log), null);
            } catch (BadLocationException ignored) {}
        }
        appender.addCallbackConsumer(this);

    }

    /**
     * This is run every time a new log comes in
     * @param log the log event passed to this callback
     */
    @Override
    public void accept(LogEvent log) {
        Document document = logs.getDocument();
        try {
            document.insertString(document.getLength(), formatLogEvent(log), null);
        } catch (BadLocationException ignored) {}
    }

    /**
     * Formats a log event and returns the string to display in the ui
     * @param evt The event to format
     * @return A formatted string created using the data from the event
     */
    private String formatLogEvent(LogEvent evt){
        return appender.getLayout().toSerializable(evt).toString();
    }

}
