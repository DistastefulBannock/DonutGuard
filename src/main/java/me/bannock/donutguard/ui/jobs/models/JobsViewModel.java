package me.bannock.donutguard.ui.jobs.models;

import me.bannock.donutguard.obf.job.JobStatus;
import me.bannock.donutguard.obf.job.ObfuscatorJob;

import java.util.Map;

public interface JobsViewModel {

    /**
     * @return A list of jobs as well as their metadata
     */
    Map<ObfuscatorJob, Map.Entry<String, JobStatus>> getJobs();

    /**
     * Cancels an obfuscation job
     * @param job The job to cancel
     */
    void cancelJob(ObfuscatorJob job);

    /**
     * Removes an obfuscation job
     * @param job The job to remove
     */
    void removeJob(ObfuscatorJob job);

}
