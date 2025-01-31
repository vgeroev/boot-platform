package org.vmalibu.module.security.service.privilege;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vmalibu.module.security.access.struct.AbstractPrivilege;
import org.vmalibu.modules.graph.GraphTraverser;

import java.util.*;

@Service
public class PrivilegeGetter {

    private final Map<String, AbstractPrivilege> privileges;

    @Autowired
    public PrivilegeGetter(List<? extends ModulePrivilegeGetter> privilegeGetters) {
        checkPrivileges(privilegeGetters);
        this.privileges = getSortedPrivileges(privilegeGetters);
    }

    public @NonNull Map<String, AbstractPrivilege> getAvailablePrivileges() {
        return privileges;
    }

    private static void checkPrivileges(List<? extends ModulePrivilegeGetter> privilegeGetters) {
        Set<String> keys = new HashSet<>();
        for (ModulePrivilegeGetter privilegeGetter : privilegeGetters) {
            for (AbstractPrivilege privilege : privilegeGetter.getPrivileges()) {
                String key = privilege.getKey();
                if (keys.contains(key)) {
                    throw new IllegalStateException("There are at least two privileges with the same key: " + key);
                }
                keys.add(key);
            }
        }
    }

    private static Map<String, AbstractPrivilege> getSortedPrivileges(List<? extends ModulePrivilegeGetter> privilegeGetters) {
        List<ModulePrivilege> modulePrivileges = new ArrayList<>();
        for (ModulePrivilegeGetter privilegeGetter : privilegeGetters) {
            for (AbstractPrivilege privilege : privilegeGetter.getPrivileges()) {
                modulePrivileges.add(new ModulePrivilege(privilege, privilegeGetter.getModuleUUID(), privilegeGetter.getModuleDependencies()));
            }
        }

        List<ModulePrivilege> sortedPrivileges = GraphTraverser.simpleDependencyTreeConstructor(
                modulePrivileges, (p1, p2) -> {
                    Set<String> dependencies = p1.moduleDependencies();
                    return dependencies.contains(p2.moduleUUID());
                });

        Map<String, AbstractPrivilege> privilegeMap = new HashMap<>(sortedPrivileges.size());
        for (ModulePrivilege modulePrivilege : sortedPrivileges) {
            AbstractPrivilege privilege = modulePrivilege.privilege();
            privilegeMap.put(privilege.getKey(), privilege);
        }

        return privilegeMap;
    }

    private record ModulePrivilege(AbstractPrivilege privilege,
                                   String moduleUUID,
                                   Set<String> moduleDependencies) {

    }

}
