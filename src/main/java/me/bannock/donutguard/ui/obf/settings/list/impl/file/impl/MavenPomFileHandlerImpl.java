package me.bannock.donutguard.ui.obf.settings.list.impl.file.impl;

import me.bannock.donutguard.ui.obf.settings.list.impl.file.CustomFileHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashSet;
import java.util.Objects;

public class MavenPomFileHandlerImpl implements CustomFileHandler {

    private final Logger logger = LogManager.getLogger();

    @Override
    public boolean handle(HashSet<File> files, File toBeProcessed) {
        Objects.requireNonNull(toBeProcessed);
        String lowerCaseName = toBeProcessed.getName();
        if (!lowerCaseName.endsWith(".xml")){
            return false;
        }
        if (!toBeProcessed.exists()){
            logger.warn(String.format("Pom file \"%s\"needs to exist",
                    toBeProcessed.getAbsolutePath()));
            throw new IllegalArgumentException("Pom file needs to exist");
        }

        // TODO: Parse and download maven pom artifacts

        return true;
    }
}
