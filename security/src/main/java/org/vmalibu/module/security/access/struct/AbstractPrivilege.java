package org.vmalibu.module.security.access.struct;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public abstract class AbstractPrivilege {

    private final String key;
    private final Set<AccessOp> accessOps;

    protected AbstractPrivilege(@NonNull String key, @NonNull AccessOp... accessOps) {
        this.key = key;

        EnumSet<AccessOp> ops = EnumSet.noneOf(AccessOp.class);
        ops.addAll(Arrays.asList(accessOps));
        this.accessOps = Collections.unmodifiableSet(ops);
    }

    public @NonNull String getKey() {
        return key;
    }

    public @NonNull Set<AccessOp> getAccessOps() {
        return accessOps;
    }

}
