package org.vmalibu.module.security.service.privilege;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vmalibu.module.security.access.AbstractPrivilege;

import java.util.List;

@Service
public class PrivilegeGetter {

    private final List<AbstractPrivilege> privileges;

    @Autowired
    public PrivilegeGetter(List<? extends ModulePrivilegeGetter> privilegeGetters) {
        this.privileges = privilegeGetters.stream()
                .flatMap(getter -> getter.getPrivileges().stream())
                .toList();
    }


    public @NonNull List<AbstractPrivilege> getAvailablePrivileges() {
        return privileges;
    }

}
