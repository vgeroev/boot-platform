package org.vmalibu.module.security.authorization.source;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface UserSource {

    long getId();

    @NonNull String getUsername();

}
