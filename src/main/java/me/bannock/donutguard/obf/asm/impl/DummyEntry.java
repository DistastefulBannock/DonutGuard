package me.bannock.donutguard.obf.asm.impl;

import me.bannock.donutguard.obf.asm.FileEntry;

public class DummyEntry extends FileEntry<String> {

    public DummyEntry(String path){
        super(path, false, false, "");
    }

    public DummyEntry() {
        this("");
    }

}
