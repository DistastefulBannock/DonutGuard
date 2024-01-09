package me.bannock.donutguard.ui.jobs;

import com.google.inject.Inject;
import me.bannock.donutguard.ui.MainFrame;
import me.bannock.donutguard.ui.jobs.views.JobsView;

import javax.swing.JFrame;

public class JobsFrame extends JFrame {

    @Inject
    public JobsFrame(MainFrame mainFrame, JobsView view){
        setTitle("Jobs");
        setSize(450, 325);
        setLocationRelativeTo(mainFrame);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(view);
    }

}
