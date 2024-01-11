package me.bannock.donutguard.obf.asm.impl;

import me.bannock.donutguard.obf.asm.FileEntry;

public class ResourceEntry extends FileEntry<byte[]> {

    public ResourceEntry(String path, boolean shouldMutate, byte[] content) {
        super(path, true, shouldMutate, content);
    }

}
