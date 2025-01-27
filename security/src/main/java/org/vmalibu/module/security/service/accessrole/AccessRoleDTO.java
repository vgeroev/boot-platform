package org.vmalibu.module.security.service.accessrole;

import lombok.Builder;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.module.security.database.domainobject.DBAccessRole;

import java.util.List;

@Builder
public record AccessRoleDTO(long id,
                            String name,
                            List<PrivilegeDTO> privileges) {

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
