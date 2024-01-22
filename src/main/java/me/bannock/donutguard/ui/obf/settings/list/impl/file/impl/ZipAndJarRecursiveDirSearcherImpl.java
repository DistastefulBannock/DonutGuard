package me.bannock.donutguard.ui.obf.settings.list.impl.file.impl;

import me.bannock.donutguard.ui.obf.settings.list.impl.file.CustomFileHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashSet;
import java.util.Objects;

public class ZipAndJarRecursiveDirSearcherImpl implements CustomFileHandler {

    private final Logger logger = LogManager.getLogger();

    @Override
    public boolean handle(HashSet<File> files, File toBeProcessed) {
        Objects.requireNonNull(toBeProcessed);
        if (!toBeProcessed.isDirectory()){
            logger.warn(String.format("File \"%s\" is not a directory.", toBeProcessed.getAbsolutePath()));
            throw new IllegalArgumentException(String.format(
                    "File \"%s\" is not a directory.", toBeProcessed.getAbsolutePath()));
        }
        else if (!toBeProcessed.exists()){
            logger.warn(String.format("File \"%s\" does not exist.", toBeProcessed.getAbsolutePath()));
            throw new IllegalArgumentException(String.format(
                    "File \"%s\" does not exist.", toBeProcessed.getAbsolutePath()));
        }

        files.addAll(getGetJarsAndZipsFromDir(toBeProcessed));
        return true;
    }

    /**
     * Recursive method that travels down directories, searching for jars and zips
     * @return A hashset of all jars and zips found under the provided dir
     */
    private HashSet<File> getGetJarsAndZipsFromDir(File dir){
        if (dir == null){
            logger.warn("Dir cannot be null.");
            throw new IllegalArgumentException("Dir cannot be null.");
        }
        if (!dir.exists() || !dir.isDirectory()){
            logger.warn(String.format("Dir \"%s\" is not valid.", dir.getAbsolutePath()));
            throw new IllegalArgumentException(String.format(
                    "Dir \"%s\" is not a valid input for this method", dir.getAbsolutePath()));
        }

        HashSet<File> foundFiles = new HashSet<>();
        File[] listFiles;
        try{
            listFiles = dir.listFiles();
            if (listFiles == null)
                listFiles = new File[0];
        }catch (NullPointerException e){
            logger.warn(String.format(
                    "Something went wrong while getting the children files for \"%s\"",
                    dir.getAbsolutePath()), e);
            throw e;
        }
        for (File file : listFiles){

            // We travel down to the subdirs of this subdir if it is a dir
            if (file.isDirectory()){
                foundFiles.addAll(getGetJarsAndZipsFromDir(file));
                continue;
            }

            // Jars are just fancy zips, so we can add zips as well
            String lowerFileName = file.getName().toLowerCase();
            if (!lowerFileName.endsWith(".zip") && !lowerFileName.endsWith(".jar"))
                continue;
            foundFiles.add(file);
        }
        return foundFiles;
    }

}
