package me.bannock.donutguard.ui.obf.settings.list.impl.file;

import java.io.File;
import java.util.HashSet;

public interface CustomFileHandler {

    /**
     * Handles a specific file type for the file list setting
     * @param files The set of files that will be added to the list
     * @param toBeProcessed The file that is currently being processed
     * @return True if the file was handled, false otherwise.
     *  If false is returned by every handler, the file will simply be added
     *  to the set.
     */
    boolean handle(HashSet<File> files, File toBeProcessed);

}
