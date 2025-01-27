package org.vmalibu.module.security.service.accessrole;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.module.security.database.domainobject.DBAccessRole;

import java.util.List;

@Builder
@Schema(description = "Access role info")
public record AccessRoleDTO(@Schema(description = "Unique identifier") long id,
                            @Schema(description = "Name of access role") String name,
                            @Schema(description = "Privileges of access role") List<PrivilegeDTO> privileges) {

    public static AccessRoleDTO from(@Nullable DBAccessRole accessRole) {
        if (accessRole == null) {
            return null;
        }

        List<PrivilegeDTO> privileges = accessRole.getPrivileges()
                .stream()
                .map(PrivilegeDTO::from)
                .toList();

        return AccessRoleDTO.builder()
                .id(accessRole.getId())
                .name(accessRole.getName())
                .privileges(privileges)
                .build();
    }
}
