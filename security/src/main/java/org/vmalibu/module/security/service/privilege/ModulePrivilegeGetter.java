package org.vmalibu.module.security.service.privilege;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.module.security.access.struct.AbstractPrivilege;

import java.util.List;
import java.util.Set;

public interface ModulePrivilegeGetter {

    @NonNull List<AbstractPrivilege> getPrivileges();

    @NonNull String getModuleUUID();

    @NonNull Set<@NonNull String> getModuleDependencies();
}
