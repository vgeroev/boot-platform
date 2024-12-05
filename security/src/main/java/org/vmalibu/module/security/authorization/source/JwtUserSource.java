package org.vmalibu.module.security.authorization.source;

import org.checkerframework.checker.nullness.qual.NonNull;

public record JwtUserSource(long id, String username, String userId) implements UserSource {

    @Override
    public long getId() {
        return id;
    }

    @Override
    public @NonNull String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "JwtUserSource[" +
                "id=" + id + ", " +
                "username=" + username + ", " +
                "userId=" + userId + ']';
    }

}
