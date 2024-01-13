package org.vmalibu.module.exercises.access;

import org.vmalibu.module.security.access.AbstractPrivilege;
import org.vmalibu.module.security.access.AccessOp;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class ExercisesPrivilege extends AbstractPrivilege {

    public static final String KEY = "exercises.exercises_privilege";
    public static final Set<AccessOp> ACCESS_OPS = Collections.unmodifiableSet(
            EnumSet.of(AccessOp.READ, AccessOp.WRITE, AccessOp.DELETE, AccessOp.EXECUTE));

    protected ExercisesPrivilege() {
        super(KEY, ACCESS_OPS.toArray(new AccessOp[0]));
    }

}
