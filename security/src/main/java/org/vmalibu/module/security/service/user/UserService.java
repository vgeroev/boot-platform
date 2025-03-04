package org.vmalibu.module.security.service.user;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.Set;

public interface UserService {

    @Nullable UserDTO findById(long id);

    @Nullable UserDTO findByUsername(@NonNull String username);

    @Nullable UserWithPrivilegesDTO findWithPrivileges(long id);

    @Nullable UserWithPrivilegesDTO findWithPrivileges(@NonNull String username);

    @NonNull UserDTO create(@NonNull String username, @NonNull String password) throws PlatformException;

    void addAccessRoles(long id, @NonNull Set<@NonNull Long> accessRoleIds) throws PlatformException;

    void removeAccessRoles(long id, @NonNull Set<@NonNull Long> accessRoleIds) throws PlatformException;
}
