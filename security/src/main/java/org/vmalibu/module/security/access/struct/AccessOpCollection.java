package org.vmalibu.module.security.access.struct;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.Serializable;
import java.util.*;

public class AccessOpCollection implements Serializable {

    private int value;

    public AccessOpCollection(int value) {
        this.value = value;
    }

    public AccessOpCollection() {
        this(0);
    }

    public AccessOpCollection(@NonNull AccessOp... ops) {
        setOps(ops);
    }

    public boolean contains(int otherValue) {
        return (value & otherValue) == otherValue;
    }

    public boolean contains(@NonNull AccessOpCollection opCollection) {
        return contains(opCollection.value);
    }

    public boolean contains(@NonNull AccessOp op) {
        return (value & op.getValue()) != 0;
    }

    public boolean contains(@NonNull AccessOp... ops) {
        for (AccessOp op : ops) {
            if (!contains(op)) {
                return false;
            }
        }
        return true;
    }

    public boolean contains(@NonNull Collection<@NonNull AccessOp> ops) {
        return contains(ops.toArray(new AccessOp[0]));
    }

    public void setOps(@NonNull AccessOp[] ops) {
        value = 0;
        addOps(ops);
    }

    public AccessOpCollection addOps(@NonNull AccessOp[] ops) {
        for (AccessOp op : ops) {
            value |= op.getValue();
        }

        return this;
    }

    public int getValue() {
        return value;
    }

    public @NonNull Set<AccessOp> toOps() {
        Set<AccessOp> result = new HashSet<>();
        for (AccessOp op : AccessOp.values()) {
            if (contains(op)) {
                result.add(op);
            }
        }

        return Collections.unmodifiableSet(result);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessOpCollection that = (AccessOpCollection) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}