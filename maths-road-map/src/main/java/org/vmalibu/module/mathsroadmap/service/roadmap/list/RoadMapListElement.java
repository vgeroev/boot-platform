package org.vmalibu.module.mathsroadmap.service.roadmap.list;

import lombok.Builder;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBRoadMap;
import org.vmalibu.module.security.service.user.UserDTO;

import java.util.Date;

@Builder
public record RoadMapListElement(long id,
                                 Date createdAt,
                                 Date updatedAt,
                                 UserDTO creator,
                                 String title,
                                 String description) {

    public static RoadMapListElement from(@Nullable DBRoadMap mathsRoadMap) {
        if (mathsRoadMap == null) {
            return null;
        }

        return RoadMapListElement.builder()
                .id(mathsRoadMap.getId())
                .createdAt(mathsRoadMap.getCreatedAt())
                .updatedAt(mathsRoadMap.getUpdatedAt())
                .creator(UserDTO.from(mathsRoadMap.getCreator()))
                .title(mathsRoadMap.getTitle())
                .description(mathsRoadMap.getDescription())
                .build();
    }

}
