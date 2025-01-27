package org.vmalibu.module.security.access.struct;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;

public class PrivilegeAuthority implements GrantedAuthority {

    private final String key;
    private final AccessOpCollection accessOpCollection;

    public PrivilegeAuthority(@NonNull String key, @NonNull AccessOpCollection accessOpCollection) {
        this.key = key;
        this.accessOpCollection = accessOpCollection;
    }

    public PrivilegeAuthority(@NonNull String key, @NonNull AccessOp... accessOps) {
        this(key, new AccessOpCollection(accessOps));
    }

    public @NonNull String getKey() {
        return key;
    }

    public @NonNull AccessOpCollection getAccessOpCollection() {
        return accessOpCollection;
    }

    @Override
    public String getAuthority() {
        return getKey();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrivilegeAuthority that = (PrivilegeAuthority) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
