package org.vmalibu.module.security.access;

import org.vmalibu.module.security.access.struct.AbstractPrivilege;
import org.vmalibu.module.security.access.struct.AccessOp;

public class UserPrivilege extends AbstractPrivilege {

    public static final UserPrivilege INSTANCE = new UserPrivilege();

    private UserPrivilege() {
        super("security_user", AccessOp.READ, AccessOp.WRITE, AccessOp.DELETE);
    }
}
