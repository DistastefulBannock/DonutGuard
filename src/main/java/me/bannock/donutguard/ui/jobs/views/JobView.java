package me.bannock.donutguard.ui.jobs.views;

import me.bannock.donutguard.obf.job.JobStatus;
import me.bannock.donutguard.obf.job.ObfuscatorJob;
import me.bannock.donutguard.ui.jobs.models.JobsViewModel;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;

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
        switch (jobStatus){
            case QUEUED:
            case RUNNING:{
                JButton cancel = new JButton("Cancel");
                cancel.addActionListener(e -> {
                    model.cancelJob(job);
                    parentview.loadJobs();
                });
                add(cancel, BorderLayout.EAST);
            }break;
            case CANCELLED:
            case COMPLETED:
            case FAILED:{
                JButton remove = new JButton("Remove");
                remove.addActionListener(e -> {
                    model.removeJob(job);
                    parentview.loadJobs();
                });
                add(remove, BorderLayout.EAST);
            }break;
        }

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

}
