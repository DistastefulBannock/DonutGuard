package me.bannock.donutguard.obf.job;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.obf.asm.JarHandler;
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

    private JarHandler jarHandler;

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

        logger.info("Creating job injector...");
        Injector injector = Guice.createInjector(new JobModule(this));
        logger.info("Successfully created job injector");
        logger.info("Creating and loading jar handler...");
        this.jarHandler = injector.getInstance(JarHandler.class);
        jarHandler.loadJarFile(getConfigDTO().input, true);
        logger.info("Successfully created and loaded jar handler");

        // We need to occasionally check for interrupts in case we need to cancel the job
        checkForInterrupt();

        jarHandler.writeJarFile(configDTO.output, configDTO.computeFrames,
                configDTO.computeMaxes, true);

        logger.info("Finished obfuscation job");
    }

    private void checkForInterrupt() throws InterruptedException {
        if (Thread.interrupted()) {
            logger.info("Obfuscation job was interrupted; likely cancelled by user.");
            throw new InterruptedException("Obfuscation job was interrupted");
        }
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

    protected ConfigDTO getConfigDTO() {
        return configDTO;
    }

    public JarHandler getJarHandler() {
        return jarHandler;
    }

}
