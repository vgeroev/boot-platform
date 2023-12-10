package org.vmalibu.module.security.authorization.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.vmalibu.module.security.access.AccessOpCollection;
import org.vmalibu.module.security.authorization.controller.privilege.PrivilegeJoinType;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ControllerAuthDetails {

    private final PrivilegeJoinType joinType;

    private final Map<String, AccessOpCollection> privileges;
}
