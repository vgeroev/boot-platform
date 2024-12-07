package org.vmalibu.module.mathsroadmap.service.roadmap;

import lombok.Builder;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBRoadMap;

import java.util.Date;

@Builder
public record RoadMapDTO(long id,
                         Date createdAt,
                         Date updatedAt,
                         long creatorId,
                         String title,
                         String description) {

    public static RoadMapDTO from(@Nullable DBRoadMap mathsRoadMap) {
        if (mathsRoadMap == null) {
            return null;
        }

        return RoadMapDTO.builder()
                .id(mathsRoadMap.getId())
                .createdAt(mathsRoadMap.getCreatedAt())
                .updatedAt(mathsRoadMap.getUpdatedAt())
                .creatorId(mathsRoadMap.getCreator().getId())
                .title(mathsRoadMap.getTitle())
                .description(mathsRoadMap.getDescription())
                .build();
    }

}
