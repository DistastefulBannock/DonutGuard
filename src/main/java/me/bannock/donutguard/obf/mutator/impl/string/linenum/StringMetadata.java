package me.bannock.donutguard.obf.mutator.impl.string.linenum;

import java.util.Objects;

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

    /**
     * Encrypts a string using its metadata
     * @return The encrypted string
     */
    public String getEncrypted(){
        char[] chars = getValue().toCharArray();
        for (int i = 0; i < chars.length; i++){
            chars[i] = (char)(chars[i] ^ (getInc() * (i + 1) * getKey()));
        }

        return new String(chars).intern();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringMetadata that = (StringMetadata) o;
        return getIndex() == that.getIndex();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIndex());
    }

}
