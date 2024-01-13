package org.vmalibu.module.exercises.service.exercisesource;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Builder;
import org.vmalibu.module.exercises.database.domainobject.DbExerciseSource;

import java.util.Date;

@JsonTypeName("exercise_source")
@Builder
public record ExerciseSourceDto(long id,
                                Date createdAt,
                                Date updatedAt,
                                String name,
                                String ownerId,
                                boolean published) {

    public static ExerciseSourceDto from(DbExerciseSource exerciseSource) {
        if (exerciseSource == null) {
            return null;
        }

        return ExerciseSourceDto.builder()
                .id(exerciseSource.getId())
                .createdAt(exerciseSource.getCreatedAt())
                .updatedAt(exerciseSource.getUpdatedAt())
                .name(exerciseSource.getName())
                .ownerId(exerciseSource.getOwnerId())
                .published(exerciseSource.isPublished())
                .build();
    }
}
