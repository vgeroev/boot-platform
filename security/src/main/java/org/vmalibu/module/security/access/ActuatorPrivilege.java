package org.vmalibu.module.security.access;

import org.vmalibu.module.security.access.struct.AbstractPrivilege;
import org.vmalibu.module.security.access.struct.AccessOp;

public class ActuatorPrivilege extends AbstractPrivilege {

    public static final ActuatorPrivilege INSTANCE = new ActuatorPrivilege();

    public ActuatorPrivilege() {
        super("security_actuator", AccessOp.READ);
    }
}
