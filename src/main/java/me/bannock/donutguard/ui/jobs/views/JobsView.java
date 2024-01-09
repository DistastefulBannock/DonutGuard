package me.bannock.donutguard.ui.jobs.views;

import com.google.inject.Inject;
import me.bannock.donutguard.ui.jobs.models.JobsViewModel;

import javax.swing.JScrollPane;

public class JobsView extends JScrollPane {

    private final JobsViewModel model;

    @Inject
    public JobsView(JobsViewModel model){
        this.model = model;
    }

    /**
     * Refreshes and loads the jobs as well as their metadata
     */
    public void loadJobs(){

    }

}
