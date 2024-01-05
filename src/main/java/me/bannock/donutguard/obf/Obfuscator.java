package me.bannock.donutguard.obf;

import com.google.common.collect.ImmutableMap;

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
    private final Map<ObfuscatorJob, Future> tasks = new LinkedHashMap<>();
    private final Map<ObfuscatorJob, String> friendlyNameTasks = new LinkedHashMap<>();

    /**
     * Request a job to be executed by the obfuscator
     * @param obfuscatorJob The job to be executed
     */
    public void submitJob(ObfuscatorJob obfuscatorJob){
        Future<?> task = executorService.submit(obfuscatorJob);
        // We create a timestamped label so the user knows which job is which in the ui
        // The Future<?> object is also kept so the user can cancel the job if they need to
        String jobLabel = getNewJobLabel();
        tasks.put(obfuscatorJob, task);
        friendlyNameTasks.put(obfuscatorJob, getNewJobLabel());
    }

    /**
     * @return The currently submitted jobs
     */
    public ImmutableMap<ObfuscatorJob, String> getTasks() {
        return ImmutableMap.copyOf(friendlyNameTasks);
    }

    /**
     * Removes a job, also cancels it
     * @param job The job to remove
     */
    public void removeJob(ObfuscatorJob job){
        Future<?> jobFuture = tasks.get(job);
        if (!jobFuture.isCancelled() && !jobFuture.isDone()){
            jobFuture.cancel(true);
        }
        tasks.remove(job);
    }

    private String getNewJobLabel(){
        return TIME_FORMAT.format(Calendar.getInstance().get(Calendar.HOUR)) + ":" +
                TIME_FORMAT.format(Calendar.getInstance().get(Calendar.MINUTE)) + ":" +
                TIME_FORMAT.format(Calendar.getInstance().get(Calendar.SECOND));
    }

}
