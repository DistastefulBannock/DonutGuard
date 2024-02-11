package me.bannock.donutguard.obf.mutator.impl.string.linenum;

public class StringMetadata {
    private final String value;
    private final int index, inc, key;
    private final MethodMetadata parentMethod;

    public StringMetadata(String value, int index, int inc, int key, MethodMetadata parentMethod) {
        this.value = value;
        this.index = index;
        this.inc = inc;
        this.key = key;
        this.parentMethod = parentMethod;
    }

    public String getValue() {
        return value;
    }

    public int getIndex() {
        return index;
    }

    public int getInc() {
        return inc;
    }

    public int getKey() {
        return key;
    }

    public MethodMetadata getParentMethod() {
        return parentMethod;
    }



}
