package org.vmalibu.module.security.service.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.module.security.database.domainobject.DBUser;

import java.util.Date;

@Builder
@Schema(description = "User's info")
public record UserDTO(@Schema(description = "Unique identifier") long id,
                      @Schema(description = "username") String username,
                      @Schema(hidden = true) @JsonIgnore String password,
                      @Schema(description = "User's creation date") Date createdAt,
                      @Schema(description = "User's update date") Date updatedAt) {

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
