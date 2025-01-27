package org.vmalibu.module.security.access.struct;

import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class AbstractPrivilege {

    private final String key;
    private final AccessOpCollection accessOpCollection;

    protected AbstractPrivilege(@NonNull String key, @NonNull AccessOp... accessOps) {
        this.key = key;
        this.accessOpCollection = new AccessOpCollection(accessOps);
    }

    public @NonNull String getKey() {
        return key;
    }

    public @NonNull AccessOpCollection getAccessOpCollection() {
        return new AccessOpCollection(accessOpCollection.getValue());
    }

}
