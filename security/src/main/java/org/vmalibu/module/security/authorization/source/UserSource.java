package org.vmalibu.module.security.authorization.source;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.security.core.Authentication;

public interface UserSource {

    @NonNull String getUserId();

    @NonNull Authentication getAuthentication();
}
