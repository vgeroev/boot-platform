package org.vmalibu.module.security.configuration.authorized;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.vmalibu.module.security.access.AccessRolePrivilege;
import org.vmalibu.module.security.access.UserPrivilege;
import org.vmalibu.module.security.access.struct.AccessOp;
import org.vmalibu.module.security.authorization.controller.privilege.AccessPermission;
import org.vmalibu.module.security.authorization.controller.privilege.PrivilegeAccess;
import org.vmalibu.module.security.authorization.controller.privilege.PrivilegeJoinType;
import org.vmalibu.module.security.authorization.source.UserSource;

import static org.vmalibu.module.security.configuration.authorized.AuthorizedSecurityConfigurationTest.*;

@RestController
public class WebTestController {

    @GetMapping(value = AUTHORIZED_CONTROLLER_PATH)
    @ResponseStatus(HttpStatus.OK)
    public UserSource authorized(UserSource userSource) {
        return userSource;
    }

    @GetMapping(value = PRIVILEGED_ONE_AND_CONTROLLER_PATH)
    @ResponseStatus(HttpStatus.OK)
    @AccessPermission(
            joinType = PrivilegeJoinType.AND,
            values = @PrivilegeAccess(
                    privilege = UserPrivilege.class,
                    ops = { AccessOp.READ, AccessOp.DELETE }
            )
    )
    public UserSource privilegedOneAnd(UserSource userSource) {
        return userSource;
    }

    @GetMapping(value = PRIVILEGED_MULTIPLE_AND_CONTROLLER_PATH)
    @ResponseStatus(HttpStatus.OK)
    @AccessPermission(
            joinType = PrivilegeJoinType.AND,
            values = {
                    @PrivilegeAccess(
                            privilege = UserPrivilege.class,
                            ops = { AccessOp.READ, AccessOp.DELETE }
                    ),
                    @PrivilegeAccess(
                            privilege = AccessRolePrivilege.class,
                            ops = { AccessOp.WRITE }
                    )
            }
    )
    public UserSource privilegedMultipleAnd(UserSource userSource) {
        return userSource;
    }

    @GetMapping(value = PRIVILEGED_ONE_OR_CONTROLLER_PATH)
    @ResponseStatus(HttpStatus.OK)
    @AccessPermission(
            joinType = PrivilegeJoinType.OR,
            values = @PrivilegeAccess(
                    privilege = UserPrivilege.class,
                    ops = { AccessOp.READ, AccessOp.DELETE }
            )
    )
    public UserSource privilegedOneOr(UserSource userSource) {
        return userSource;
    }

    @GetMapping(value = PRIVILEGED_MULTIPLE_OR_CONTROLLER_PATH)
    @ResponseStatus(HttpStatus.OK)
    @AccessPermission(
            joinType = PrivilegeJoinType.OR,
            values = {
                    @PrivilegeAccess(
                            privilege = UserPrivilege.class,
                            ops = { AccessOp.READ, AccessOp.DELETE }
                    ),
                    @PrivilegeAccess(
                            privilege = AccessRolePrivilege.class,
                            ops = { AccessOp.WRITE }
                    )
            }
    )
    public UserSource privilegedMultipleOr(UserSource userSource) {
        return userSource;
    }
}
