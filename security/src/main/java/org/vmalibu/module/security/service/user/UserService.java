package org.vmalibu.module.security.service.user;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.modules.module.exception.PlatformException;

public interface UserService {

    @Nullable UserDTO findById(long id);

    @Nullable UserDTO findByUsername(@NonNull String username);

    @NonNull UserDTO create(@NonNull String username, @NonNull String password) throws PlatformException;
}
