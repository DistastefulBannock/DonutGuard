package me.bannock.donutguard.ui.topnav.controllers;

import com.google.inject.Injector;
import me.bannock.donutguard.obf.Obfuscator;
import me.bannock.donutguard.obf.job.ObfuscatorJob;
import me.bannock.donutguard.ui.about.AboutFrame;
import me.bannock.donutguard.ui.jobs.JobsFrame;
import me.bannock.donutguard.ui.topnav.TopNavView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.SwingUtilities;

public class TopNavController {

    private final Logger logger = LogManager.getLogger();

    public TopNavController(Injector injector, TopNavView topNavView){
        topNavView.getSaveConfig().addActionListener(injector.getInstance(SaveConfigActionListenerImpl.class));
        topNavView.getLoadConfig().addActionListener(injector.getInstance(LoadConfigActionListenerImpl.class));

        topNavView.getViewObfuscationJobs()
                .addActionListener(evt -> {
                    logger.info("Opening a new jobs frame...");
                    JobsFrame jobsFrame = injector.getInstance(JobsFrame.class);
                    jobsFrame.setVisible(true);
                    logger.info("Successfully opened a new jobs frame");
                });

        topNavView.getStart().addActionListener(evt -> createJobAndRun(injector));

        topNavView.getAbout().addActionListener(evt -> SwingUtilities
                .invokeLater(() -> {
                    logger.info("Opening a new about frame...");
                    AboutFrame aboutFrame = injector.getInstance(AboutFrame.class);
                    aboutFrame.setVisible(true);
                    logger.info("Successfully opened a new about frame");
                }));
    }

    private void createJobAndRun(Injector injector){
        logger.info("Creating and queuing a new obfuscator job...");
        ObfuscatorJob job = injector.getInstance(ObfuscatorJob.class);
        injector.getInstance(Obfuscator.class).submitJob(job);
        logger.info("Successfully created and queued a new obfuscator job");
    }

}
