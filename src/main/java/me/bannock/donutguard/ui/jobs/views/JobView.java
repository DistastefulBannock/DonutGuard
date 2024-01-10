package me.bannock.donutguard.ui.jobs.views;

import me.bannock.donutguard.obf.job.JobStatus;
import me.bannock.donutguard.obf.job.ObfuscatorJob;
import me.bannock.donutguard.ui.jobs.models.JobsViewModel;
import me.bannock.donutguard.utils.UiUtils;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.io.File;

public class JobView extends JPanel {

    public JobView(JobsViewModel model, JobsView parentview, ObfuscatorJob job,
                   String jobName, JobStatus jobStatus){
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0, 0, 8, 0));

        // This is the label that will be displayed on the left side of the panel
        add(new JLabel("<html>" + jobName +
                " <font color=" + getColorFromStatus(jobStatus) +
                ">" + jobStatus + "</font></html>"), BorderLayout.WEST);

        // Buttons appear on the right side under certain conditions
        // This panel allows us to stack multiple buttons side by side
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));

        { // Log file button should appear if a log file is available. This is temporary until a log viewer is implemented
            File logFile = getLogFileLocation(jobName);
            if (logFile.exists()) {
                JButton logFileButton = new JButton("Open Log");
                logFileButton.addActionListener(e -> UiUtils.openFile(logFile));
                buttons.add(logFileButton);
            }
        }

        switch (jobStatus){
            case QUEUED:
            case RUNNING:{
                JButton cancel = new JButton("Cancel");
                cancel.addActionListener(e -> {
                    model.cancelJob(job);
                    parentview.loadJobs();
                });
                buttons.add(cancel);
            }break;
            case CANCELLED:
            case COMPLETED:
            case FAILED:{
                JButton remove = new JButton("Remove");
                remove.addActionListener(e -> {
                    model.removeJob(job);
                    parentview.loadJobs();
                });
                buttons.add(remove);
            }break;
        }
        add(buttons, BorderLayout.EAST);

    }

    private String getColorFromStatus(JobStatus status){
        switch (status){
            case QUEUED: return "gray";
            case RUNNING: return "orange";
            case COMPLETED: return "green";
            case FAILED:
            case CANCELLED:
            case NOT_FOUND: return "red";
            default: return "black";
        }
    }

    /**
     * @param jobName The job name to get the log file for
     * @return The log file for the given job name
     */
    private File getLogFileLocation(String jobName){
        return new File("logs/DonutGuard " + jobName + ".log");
    }

}
