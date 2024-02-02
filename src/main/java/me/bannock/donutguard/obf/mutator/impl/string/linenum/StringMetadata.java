package me.bannock.donutguard.obf.mutator.impl.string.linenum;

import java.util.Objects;

public class StringMetadata {
    private final String value;
    private final int index, xor, key;
    private final MethodMetadata parentMethod;

    public StringMetadata(String value, int index, int xor, int key, MethodMetadata parentMethod) {
        this.value = value;
        this.index = index;
        this.xor = xor;
        this.key = key;
        this.parentMethod = parentMethod;
    }

    public String getValue() {
        return value;
    }

    public int getIndex() {
        return index;
    }

    public int getXor() {
        return xor;
    }

    public int getKey() {
        return key;
    }

    public MethodMetadata getParentMethod() {
        return parentMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringMetadata that = (StringMetadata) o;
        return getIndex() == that.getIndex() && getXor() == that.getXor() && getKey() == that.getKey() && Objects.equals(getValue(), that.getValue()) && Objects.equals(getParentMethod(), that.getParentMethod());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue(), getIndex(), getXor(), getKey(), getParentMethod());
    }

}
