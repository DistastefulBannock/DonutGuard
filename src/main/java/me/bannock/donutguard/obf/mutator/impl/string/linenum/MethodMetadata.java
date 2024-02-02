package me.bannock.donutguard.obf.mutator.impl.string.linenum;

import java.util.Objects;

/**
 * We modify the methods in this mutator, and this class is used
 * to help us keep track of the changes.
 */
public class MethodMetadata {
    private final int startingLineNumber, incrementAmount, xorValue;

    public MethodMetadata(int startingLineNumber, int incrementAmount, int xorValue) {
        this.startingLineNumber = startingLineNumber;
        this.incrementAmount = incrementAmount;
        this.xorValue = xorValue;
    }

    public int getStartingLineNumber() {
        return startingLineNumber;
    }

    public int getIncrementAmount() {
        return incrementAmount;
    }

    public int getXorValue() {
        return xorValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodMetadata that = (MethodMetadata) o;
        return getStartingLineNumber() == that.getStartingLineNumber() && getIncrementAmount() == that.getIncrementAmount() && getXorValue() == that.getXorValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStartingLineNumber(), getIncrementAmount(), getXorValue());
    }

}
