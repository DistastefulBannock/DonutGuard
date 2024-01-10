package me.bannock.donutguard.ui.jobs.views;

import com.google.inject.Inject;
import me.bannock.donutguard.obf.job.JobStatus;
import me.bannock.donutguard.obf.job.ObfuscatorJob;
import me.bannock.donutguard.ui.jobs.models.JobsViewModel;
import me.bannock.donutguard.utils.ResourceUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.util.Map;

public class JobsView extends JPanel {

    private final Logger logger = LogManager.getLogger();
    private final JobsViewModel model;
    private final JPanel jobsPanel;

    @Inject
    public JobsView(JobsViewModel model){
        this.model = model;

        setLayout(new BorderLayout());

        // The container will add a scrollbar to the panel if needed
        JScrollPane jobsContainer = new JScrollPane();
        jobsContainer.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jobsContainer.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // This container will ensure that the individual job views are placed at the top of the panel
        JPanel jobsContainerWrapper = new JPanel(new BorderLayout());
        jobsContainer.setViewportView(jobsContainerWrapper);

        // Double buffered because this panel will be getting refreshed
        this.jobsPanel = new JPanel(true);
        jobsPanel.setLayout(new BoxLayout(jobsPanel, BoxLayout.Y_AXIS));
        jobsContainerWrapper.add(jobsPanel, BorderLayout.NORTH);
        loadJobs();

        JPanel refreshButtonPanel = new JPanel();
        refreshButtonPanel.setLayout(new BorderLayout());
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> loadJobs());
        refreshButtonPanel.add(refresh, BorderLayout.EAST);

        add(jobsContainer, BorderLayout.CENTER);
        add(refreshButtonPanel, BorderLayout.SOUTH);
    }

    /**
     * Refreshes and loads the jobs as well as their metadata
     */
    public void loadJobs(){
        logger.info("Loading jobs...");
        jobsPanel.removeAll();

        Map<ObfuscatorJob, Map.Entry<String, JobStatus>> jobs = model.getJobs();
        for (ObfuscatorJob job : jobs.keySet()){
            Map.Entry<String, JobStatus> metadata = jobs.get(job);
            jobsPanel.add(new JobView(model, this, job,
                    metadata.getKey(), metadata.getValue()));
        }

        // Show a message if there are no jobs
        if(jobs.isEmpty()){
            jobsPanel.add(new JLabel(ResourceUtils.readString("ui/jobs/notice.html")));
        }

        jobsPanel.revalidate();
        jobsPanel.repaint();
        logger.info("Successfully loaded jobs");
    }

}
