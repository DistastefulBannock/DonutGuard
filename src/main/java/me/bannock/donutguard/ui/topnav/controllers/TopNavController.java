package me.bannock.donutguard.ui.topnav.controllers;

import com.google.inject.Injector;
import me.bannock.donutguard.obf.Obfuscator;
import me.bannock.donutguard.obf.job.ObfuscatorJob;
import me.bannock.donutguard.ui.topnav.TopNavView;
import me.bannock.donutguard.ui.about.AboutFrame;
import me.bannock.donutguard.ui.jobs.JobsFrame;

import javax.swing.SwingUtilities;

public class TopNavController {

    public TopNavController(Injector injector, TopNavView topNavView){
        // TODO: Implement config saving and loading
        topNavView.getSaveConfig().addActionListener(evt -> System.err.println("TODO"));
        topNavView.getLoadConfig().addActionListener(ect -> System.err.println("TODO"));

        // TODO: Implement processing as well as processing settings
        topNavView.getProcessingSettings().addActionListener(evt -> System.err.println("TODO"));

        topNavView.getViewObfuscationJobs()
                .addActionListener(evt -> injector.getInstance(JobsFrame.class).setVisible(true));

        topNavView.getStart()
                .addActionListener(evt -> createJobAndRun(injector));

        // TODO: Implement settings
        topNavView.getSettings().addActionListener(evt -> System.err.println("TODO"));

        topNavView.getAbout().addActionListener(evt -> SwingUtilities
                .invokeLater(() -> injector.getInstance(AboutFrame.class).setVisible(true)));
    }

    private void createJobAndRun(Injector injector){
        ObfuscatorJob job = injector.getInstance(ObfuscatorJob.class);
        injector.getInstance(Obfuscator.class).submitJob(job);
    }

}
