package me.bannock.donutguard.obf.job;

import com.google.inject.Module;
import me.bannock.donutguard.obf.ConfigDTO;

public interface ObfuscatorJobFactory {
    /**
     * Creates a new obfuscator job instance
     * @param config The configuration that the job should use
     * @param jobModulePlugins The job module plugin should contain any third party modules that you want to use.
     *                         This is used so you can create mutators inside your own project
     * @return The job instance
     */
    ObfuscatorJob create(ConfigDTO config, Module... jobModulePlugins);
}
