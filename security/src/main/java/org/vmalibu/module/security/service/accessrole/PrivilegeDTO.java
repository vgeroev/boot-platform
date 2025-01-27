package org.vmalibu.module.security.service.accessrole;

import lombok.Builder;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.module.security.access.struct.AccessOp;
import org.vmalibu.module.security.database.domainobject.DBPrivilege;

import java.util.Set;

@Builder
public record PrivilegeDTO(String key,
                           Set<AccessOp> accessOps) {

    public static @Nullable PrivilegeDTO from(@Nullable DBPrivilege privilege) {
        if (privilege == null) {
            return null;
        }

        return PrivilegeDTO.builder()
                .key(privilege.getKey())
                .accessOps(privilege.getValue())
                .build();
    }
}
