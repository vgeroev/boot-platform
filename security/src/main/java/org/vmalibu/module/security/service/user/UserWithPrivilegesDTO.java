package org.vmalibu.module.security.service.user;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.module.security.access.struct.AccessOp;

import java.util.Map;
import java.util.Set;

public record UserWithPrivilegesDTO(@NonNull UserDTO user, @NonNull Map<String, Set<AccessOp>> privileges) {
}
