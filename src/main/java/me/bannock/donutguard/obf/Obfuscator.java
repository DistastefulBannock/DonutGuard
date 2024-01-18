package me.bannock.donutguard.obf;

import com.google.common.collect.ImmutableMap;
import me.bannock.donutguard.logging.DonutAppenderManager;
import me.bannock.donutguard.obf.job.JobStatus;
import me.bannock.donutguard.obf.job.ObfuscatorJob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Obfuscator {

    private static final int THREAD_POOL_SIZE = 5;
    private final Logger logger = LogManager.getLogger();
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
        logger.info("New job submitted: " + obfuscatorJob);
        String jobLabel = getNewJobLabel();
        logger.info("Assigning job label \"" + jobLabel + "\" to job " + obfuscatorJob);
        obfuscatorJob.setThreadId(jobLabel); // The thread id is used for logging purposes
        logger.info("Submitting job " + jobLabel + " to the executor service...");
        Future<?> task = executorService.submit(obfuscatorJob);
        logger.info("Successfully submitted job " + jobLabel + " to the executor service");
        // We create a timestamped label so the user knows which job is which in the ui
        // The Future<?> object is also kept so the user can cancel the job if they need to
        jobs.put(obfuscatorJob, task);
        friendlyNameJobs.put(obfuscatorJob, jobLabel);
        logger.info("Finished job submission routine for " + jobLabel);
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
        logger.info("Removing job " + friendlyNameJobs.get(job) + "...");
        Future<?> jobFuture = jobs.get(job);
        if (!jobFuture.isCancelled() && !jobFuture.isDone()){
            logger.info("Cancelling job " + friendlyNameJobs.get(job) + "...");
            jobFuture.cancel(true);
            logger.info("Successfully cancelled job " + friendlyNameJobs.get(job));
        }
        jobs.remove(job);
        String cacheFriendlyName = friendlyNameJobs.get(job);
        friendlyNameJobs.remove(job);

        // Save memory on appenders that are no longer in use
        DonutAppenderManager.stopManagingAppender(job.getThreadId());

        logger.info("Successfully removed job " + cacheFriendlyName);
    }

    /**
     * Cancels a job
     * @param job The job to cancel
     */
    public void cancelJob(ObfuscatorJob job){
        logger.info("Cancelling job " + friendlyNameJobs.get(job) + "...");
        Future<?> jobFuture = jobs.get(job);
        if (!jobFuture.isCancelled() && !jobFuture.isDone()){
            jobFuture.cancel(true);
        }
        logger.info("Successfully cancelled job " + friendlyNameJobs.get(job));
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
        }else if (job.hasFailed()){
            return JobStatus.FAILED;
        }else if(jobFuture.isDone()){
            return JobStatus.COMPLETED;
        }else if (!job.hasStarted()) {
            return JobStatus.QUEUED;
        }else{
            return JobStatus.RUNNING;
        }
    }

    /**
     * Creates and returns a new job label. Also takes at least 2 milliseconds to execute
     * @return The new job label
     */
    private String getNewJobLabel(){
        logger.info("Generating a new job label...");
        long time = System.currentTimeMillis();
        // I really don't want to risk giving two threads the same id, so we sleep for 2 millis
        // here to make sure the next call will produce a different outcome
        try{
            Thread.sleep(2);
        }catch (Exception ignored){}
        String label = "Obf-job 0x" + Long.toHexString(time).toUpperCase();
        logger.info("Successfully generated a new job label: " + label);
        return label;
    }

}
