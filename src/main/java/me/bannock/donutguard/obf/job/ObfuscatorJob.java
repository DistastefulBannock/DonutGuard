package me.bannock.donutguard.obf.job;

import com.google.inject.Inject;
import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.obf.Obfuscator;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.util.concurrent.ThreadLocalRandom;

public class ObfuscatorJob implements Runnable {

    private final Logger logger = LogManager.getLogger();
    private String threadId = "Obfuscator Job " + System.currentTimeMillis() + "." + System.nanoTime(); // This will be replaced by the obfuscator

    private final ConfigDTO configDTO;
    private final Obfuscator obfuscator;

    private boolean hasStarted = false;

    @Inject
    public ObfuscatorJob(ConfigDTO configDTO, Obfuscator obfuscator){
        this.configDTO = SerializationUtils.clone(configDTO);
        this.obfuscator = obfuscator;
    }

    @Override
    public void run() {
        ThreadContext.remove("threadId");
        ThreadContext.put("threadId", threadId);
        logger.info("Starting obfuscation job...");
        hasStarted = true;

        // TODO: Write obfuscator and implement log4j so we
        //  can create a different console window for each job

        try{
            Thread.sleep(ThreadLocalRandom.current().nextLong(100, 30000));
//            Thread.sleep(2500);
        }catch (Exception e){
            logger.warn("Interrupted while sleeping", e);
        }

        logger.info("Finished obfuscation job");
        ThreadContext.remove("threadId");
    }

    public boolean hasStarted() {
        return hasStarted;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }
}
