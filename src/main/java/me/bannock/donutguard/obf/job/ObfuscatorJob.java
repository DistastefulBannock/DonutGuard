package me.bannock.donutguard.obf.job;

import com.google.inject.Inject;
import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.obf.Obfuscator;
import org.apache.commons.lang3.SerializationUtils;

import java.util.concurrent.ThreadLocalRandom;

public class ObfuscatorJob implements Runnable {

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
        hasStarted = true;

        // TODO: Write obfuscator and implement log4j so we
        //  can create a different console window for each job

        try{
            Thread.sleep(ThreadLocalRandom.current().nextLong(100, 30000));
//            Thread.sleep(2500);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public boolean hasStarted() {
        return hasStarted;
    }
}
