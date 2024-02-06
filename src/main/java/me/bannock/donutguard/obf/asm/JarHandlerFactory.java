package me.bannock.donutguard.obf.asm;

import me.bannock.donutguard.obf.config.Configuration;

public interface JarHandlerFactory {

    JarHandler create(Configuration configuration);

}
