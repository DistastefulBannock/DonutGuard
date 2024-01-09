package me.bannock.donutguard.ui.jobs.models;

import com.google.inject.Inject;
import me.bannock.donutguard.obf.Obfuscator;
import me.bannock.donutguard.obf.job.JobStatus;
import me.bannock.donutguard.obf.job.ObfuscatorJob;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class JobsViewModelImpl implements JobsViewModel {

    private final Obfuscator obfuscator;

    @Inject
    public JobsViewModelImpl(Obfuscator obfuscator){
        this.obfuscator = obfuscator;
    }

    @Override
    public Map<ObfuscatorJob, Map.Entry<String, JobStatus>> getJobs() {
        // The service only returns the map with some of the data we want,
        // so we loop over it and make several more requests to retrieve
        // the statuses
        Map<ObfuscatorJob, String> jobs = obfuscator.getJobs();
        Map<ObfuscatorJob, Map.Entry<String, JobStatus>> jobsWithMetadata = new LinkedHashMap<>();
        for (ObfuscatorJob job : jobs.keySet()){
            String jobLabel = jobs.get(job);
            JobStatus status = obfuscator.getJobStatus(job);
            Map.Entry<String, JobStatus> metadata =
                    new AbstractMap.SimpleEntry<>(jobLabel, status);
            jobsWithMetadata.put(job, metadata);
        }
        return jobsWithMetadata;
    }
}
