package me.bannock.donutguard.obf.mutator.impl.string.linenum;

import java.util.Objects;

/**
 * We modify the methods in this mutator, and this class is used
 * to help us keep track of the changes.
 */
public class MethodMetadata {
    private final int startingLineNumber, incrementAmount;

    public MethodMetadata(int startingLineNumber, int incrementAmount) {
        this.startingLineNumber = startingLineNumber;
        this.incrementAmount = incrementAmount;
    }

    public int getStartingLineNumber() {
        return startingLineNumber;
    }

    public int getIncrementAmount() {
        return incrementAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodMetadata that = (MethodMetadata) o;
        return getStartingLineNumber() == that.getStartingLineNumber() && getIncrementAmount() == that.getIncrementAmount();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStartingLineNumber(), getIncrementAmount());
    }

}
