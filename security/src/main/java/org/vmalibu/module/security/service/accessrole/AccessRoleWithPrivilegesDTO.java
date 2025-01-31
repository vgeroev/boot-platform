package org.vmalibu.module.security.service.accessrole;

import io.swagger.v3.oas.annotations.media.Schema;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.module.security.access.struct.AccessOp;
import org.vmalibu.module.security.database.domainobject.DBAccessRole;

import java.util.Map;
import java.util.Set;

@Schema(description = "Access role with privileges info")
public record AccessRoleWithPrivilegesDTO(
        @Schema(description = "Access role basic info") AccessRoleDTO accessRole,
        @Schema(description = "Privileges") Map<String, Set<AccessOp>> privileges
) {

    public static AccessRoleWithPrivilegesDTO from(@Nullable DBAccessRole accessRole) {
        if (accessRole == null) {
            return null;
        }

        return new AccessRoleWithPrivilegesDTO(AccessRoleDTO.from(accessRole), accessRole.getPrivileges());
    }
}
