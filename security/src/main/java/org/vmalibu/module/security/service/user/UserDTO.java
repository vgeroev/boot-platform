package org.vmalibu.module.security.service.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.module.security.database.domainobject.DBUser;

import java.util.Date;

@Builder
public record UserDTO(long id,
                      String username,
                      @JsonIgnore String password,
                      Date createdAt,
                      Date updatedAt) {

    public static UserDTO from(@Nullable DBUser user) {
        if (user == null) {
            return null;
        }

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
