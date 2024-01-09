package me.bannock.donutguard.obf.job;

import com.google.inject.Inject;
import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.obf.Obfuscator;
import org.apache.commons.lang3.SerializationUtils;

import java.util.concurrent.ThreadLocalRandom;

public class ObfuscatorJob implements Runnable {

    private final ConfigDTO configDTO;
    private final Obfuscator obfuscator;

    @Inject
    public ObfuscatorJob(ConfigDTO configDTO, Obfuscator obfuscator){
        this.configDTO = SerializationUtils.clone(configDTO);
        this.obfuscator = obfuscator;
    }

    @Override
    public void run() {

        try{
            Thread.sleep(ThreadLocalRandom.current().nextLong(100, 30000));
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
