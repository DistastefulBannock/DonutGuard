package me.bannock.donutguard.controllers;

import com.google.inject.Injector;
import me.bannock.donutguard.obf.Obfuscator;
import me.bannock.donutguard.obf.ObfuscatorJob;
import me.bannock.donutguard.views.TopNavView;
import me.bannock.donutguard.views.about.AboutFrame;

import javax.swing.SwingUtilities;

public class TopNavController {

    public TopNavController(Injector injector, TopNavView topNavView){
        // TODO: Implement config saving and loading
        topNavView.getSaveConfig().addActionListener(evt -> System.err.println("TODO"));
        topNavView.getLoadConfig().addActionListener(ect -> System.err.println("TODO"));

        // TODO: Implement processing as well as processing settings
        topNavView.getProcessingSettings().addActionListener(evt -> System.err.println("TODO"));
        topNavView.getViewObfuscationJobs().addActionListener(evt -> System.err.println("TODO"));

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
