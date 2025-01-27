package org.vmalibu.module.security.service.accessrole;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.module.security.access.struct.AccessOp;
import org.vmalibu.module.security.database.domainobject.DBPrivilege;

import java.util.Set;

@Builder
@Schema(description = "Privileges of access roles")
public record PrivilegeDTO(@Schema(description = "Privilege unique key") String key,
                           @Schema(description = "Access operations") Set<AccessOp> accessOps) {

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
