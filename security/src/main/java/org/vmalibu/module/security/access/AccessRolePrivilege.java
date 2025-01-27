package org.vmalibu.module.security.access;

import org.vmalibu.module.security.access.struct.AbstractPrivilege;
import org.vmalibu.module.security.access.struct.AccessOp;

public class AccessRolePrivilege extends AbstractPrivilege {

    public static final AccessRolePrivilege INSTANCE = new AccessRolePrivilege();

    private AccessRolePrivilege() {
        super("security_access_role", AccessOp.READ, AccessOp.WRITE, AccessOp.DELETE);
    }

}
