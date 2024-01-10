package me.bannock.donutguard.obf.job;

import com.google.inject.Inject;
import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.utils.UiUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

public class ObfuscatorJob implements Runnable {

    private final Logger logger = LogManager.getLogger();
    private String threadId = "Obfuscator Job " + System.currentTimeMillis() + "." + System.nanoTime(); // This will be replaced by the obfuscator

    private final ConfigDTO configDTO;

    private boolean hasStarted = false, failed = false;

    @Inject
    public ObfuscatorJob(ConfigDTO configDTO){
        this.configDTO = SerializationUtils.clone(configDTO);
    }

    @Override
    public void run() {
        ThreadContext.remove("threadId");
        ThreadContext.put("threadId", threadId);
        hasStarted = true;

        try {
            runObfuscator();
        }
        catch (Exception e) {
            if (!(e instanceof InterruptedException)) {
                failed = true;
                logger.error("An error occurred while running the obfuscator", e);
                UiUtils.showErrorMessage("Obfuscator Error", "An error occurred while running the obfuscator." +
                        "\nCheck the logs for more information.");
            }
        }

        ThreadContext.remove("threadId");
    }

    /**
     * Runs the obfuscator
     * @throws Exception If an error occurs while running the obfuscator
     */
    private void runObfuscator() throws Exception {
        logger.info("Starting obfuscation job...");
        // TODO: Write obfuscator and implement log4j so we
        //  can create a different console window for each job
        //  !!! Remember to add code to check for Thread.interrupted()
        //      so the job cancels when the users requests it
        logger.info("Finished obfuscation job");
    }

    public boolean hasStarted() {
        return hasStarted;
    }

    public boolean hasFailed() {
        return failed;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }
}
