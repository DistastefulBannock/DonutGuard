package me.bannock.donutguard.obf;

import com.google.common.collect.ImmutableMap;
import me.bannock.donutguard.obf.job.JobStatus;
import me.bannock.donutguard.obf.job.ObfuscatorJob;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Obfuscator {

    private static final int THREAD_POOL_SIZE = 5;
    private static final DecimalFormat TIME_FORMAT = new DecimalFormat("00");

    private final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
            THREAD_POOL_SIZE, THREAD_POOL_SIZE, 0L,
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    private final Map<ObfuscatorJob, Future<?>> jobs = new LinkedHashMap<>();
    private final Map<ObfuscatorJob, String> friendlyNameJobs = new LinkedHashMap<>();

    /**
     * Request a job to be executed by the obfuscator
     * @param obfuscatorJob The job to be executed
     */
    public void submitJob(ObfuscatorJob obfuscatorJob){
        Future<?> task = executorService.submit(obfuscatorJob);
        // We create a timestamped label so the user knows which job is which in the ui
        // The Future<?> object is also kept so the user can cancel the job if they need to
        String jobLabel = getNewJobLabel();
        jobs.put(obfuscatorJob, task);
        friendlyNameJobs.put(obfuscatorJob, getNewJobLabel());
    }

    /**
     * @return The currently submitted jobs
     */
    public ImmutableMap<ObfuscatorJob, String> getJobs() {
        return ImmutableMap.copyOf(friendlyNameJobs);
    }

    /**
     * Removes a job, also cancels it
     * @param job The job to remove
     */
    public void removeJob(ObfuscatorJob job){
        Future<?> jobFuture = jobs.get(job);
        if (!jobFuture.isCancelled() && !jobFuture.isDone()){
            jobFuture.cancel(true);
        }
        jobs.remove(job);
        friendlyNameJobs.remove(job);
    }

    /**
     * Gets the status of a job
     * @param job The job to get the status of
     * @return The status of the job
     */
    public JobStatus getJobStatus(ObfuscatorJob job){
        Future<?> jobFuture = jobs.get(job);
        if (jobFuture == null){
            return JobStatus.NOT_FOUND;
        }else if(jobFuture.isCancelled()){
            return JobStatus.CANCELLED;
        }else if(jobFuture.isDone()){
            return JobStatus.COMPLETED;
        }else{
            return JobStatus.RUNNING;
        }
    }

    private String getNewJobLabel(){
        return TIME_FORMAT.format(Calendar.getInstance().get(Calendar.HOUR)) + ":" +
                TIME_FORMAT.format(Calendar.getInstance().get(Calendar.MINUTE)) + ":" +
                TIME_FORMAT.format(Calendar.getInstance().get(Calendar.SECOND));
    }

}
