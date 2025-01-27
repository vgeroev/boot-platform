package org.vmalibu.module.security.access.struct;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;
import java.util.Set;

public class PrivilegeAuthority implements GrantedAuthority {

    private final String key;
    private final Set<AccessOp> accessOps;

    public PrivilegeAuthority(@NonNull String key, @NonNull Set<AccessOp> accessOps) {
        this.key = key;
        this.accessOps = Set.copyOf(accessOps);
    }

    public PrivilegeAuthority(@NonNull String key, @NonNull AccessOp... accessOps) {
        this(key, Set.of(accessOps));
    }

    public @NonNull String getKey() {
        return key;
    }

    public @NonNull Set<AccessOp> getAccessOps() {
        return accessOps;
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
