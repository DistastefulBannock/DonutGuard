package me.bannock.donutguard.obf.asm.entry.impl;

import me.bannock.donutguard.obf.asm.entry.FileEntry;

public class ResourceEntry extends FileEntry<byte[]> {

    public ResourceEntry(String path, boolean shouldMutate, boolean libraryEntry, byte[] content) {
        super(path, shouldMutate, libraryEntry, content);
    }

}
