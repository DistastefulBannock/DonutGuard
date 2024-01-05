package me.bannock.donutguard.obf;

import com.google.inject.Inject;
import org.apache.commons.lang3.SerializationUtils;

public class ObfuscatorJob implements Runnable {

    private ConfigDTO configDTO;
    private Obfuscator obfuscator;

    @Inject
    public ObfuscatorJob(ConfigDTO configDTO, Obfuscator obfuscator){
        this.configDTO = SerializationUtils.clone(configDTO);
    }

    @Override
    public void run() {

        // We now remove this job from our list of jobs to prevent a memory leak
        obfuscator.removeJob(this);
    }
}
