package org.vmalibu.module.security.authorization.source;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface UserSource {

    @NonNull String getUserId();

    @NonNull String getUsername();

}
