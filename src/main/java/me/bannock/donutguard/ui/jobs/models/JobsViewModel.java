package me.bannock.donutguard.ui.jobs.models;

import me.bannock.donutguard.obf.job.JobStatus;
import me.bannock.donutguard.obf.job.ObfuscatorJob;

import java.util.Map;

public interface JobsViewModel {

    /**
     * @return A list of jobs as well as their metadata inside of a map
     */
    Map<ObfuscatorJob, Map.Entry<String, JobStatus>> getJobs();

}
