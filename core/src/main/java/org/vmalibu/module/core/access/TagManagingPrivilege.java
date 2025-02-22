package org.vmalibu.module.core.access;

import org.vmalibu.module.security.access.struct.AbstractPrivilege;
import org.vmalibu.module.security.access.struct.AccessOp;

public class TagManagingPrivilege extends AbstractPrivilege {

    public static final TagManagingPrivilege INSTANCE = new TagManagingPrivilege();

    private TagManagingPrivilege() {
        super("core_tag_managing", AccessOp.WRITE, AccessOp.DELETE);
    }

}
