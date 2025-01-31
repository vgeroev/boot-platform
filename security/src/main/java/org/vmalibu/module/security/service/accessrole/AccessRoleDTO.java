package org.vmalibu.module.security.service.accessrole;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.module.security.database.domainobject.DBAccessRole;

@Builder
@Schema(description = "Access role info")
public record AccessRoleDTO(@Schema(description = "Unique identifier") long id,
                            @Schema(description = "Name of access role") String name,
                            @Schema(description = "Is admin access role?") boolean admin) {

    public static AccessRoleDTO from(@Nullable DBAccessRole accessRole) {
        if (accessRole == null) {
            return null;
        }

        return AccessRoleDTO.builder()
                .id(accessRole.getId())
                .name(accessRole.getName())
                .admin(accessRole.isAdmin())
                .build();
    }
}
