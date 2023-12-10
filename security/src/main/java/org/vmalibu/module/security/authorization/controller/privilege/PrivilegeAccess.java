package org.vmalibu.module.security.authorization.controller.privilege;

import org.vmalibu.module.security.access.AbstractPrivilege;
import org.vmalibu.module.security.access.AccessOp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrivilegeAccess {

    Class<? extends AbstractPrivilege> privilege() default AbstractPrivilege.class;
    AccessOp[] ops() default {};

}
