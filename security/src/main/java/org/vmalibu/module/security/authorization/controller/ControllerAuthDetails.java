package org.vmalibu.module.security.authorization.controller;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.module.security.access.struct.AccessOp;
import org.vmalibu.module.security.authorization.controller.privilege.PrivilegeJoinType;

import java.util.Map;
import java.util.Set;

public record ControllerAuthDetails(@NonNull PrivilegeJoinType joinType, @NonNull Map<String, Set<AccessOp>> privileges) {

}
