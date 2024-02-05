package me.bannock.donutguard.obf.asm.entry.impl;

import me.bannock.donutguard.obf.asm.entry.FileEntry;

public class DummyEntry extends FileEntry<String> {

    public DummyEntry(String path){
        super(path, false, false, "");
    }

    public DummyEntry() {
        this("");
    }

}
