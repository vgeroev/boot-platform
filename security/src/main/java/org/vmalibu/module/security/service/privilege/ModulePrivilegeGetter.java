package org.vmalibu.module.security.service.privilege;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.module.security.access.AbstractPrivilege;

import java.util.List;

public interface ModulePrivilegeGetter {

    @NonNull List<AbstractPrivilege> getPrivileges();
}
