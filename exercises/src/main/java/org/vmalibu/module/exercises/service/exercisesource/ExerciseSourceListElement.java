package org.vmalibu.module.exercises.service.exercisesource;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Builder;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.module.exercises.database.domainobject.DbExerciseSource;

import java.util.Date;

@JsonTypeName("exercise_source_list_element")
@Builder
public record ExerciseSourceListElement(long id, Date createdAt, Date updatedAt, String name) {

    public static @Nullable ExerciseSourceListElement from(@Nullable DbExerciseSource exerciseSource) {
        if (exerciseSource == null) {
            return null;
        }

        return ExerciseSourceListElement.builder()
                .id(exerciseSource.getId())
                .createdAt(exerciseSource.getCreatedAt())
                .updatedAt(exerciseSource.getUpdatedAt())
                .name(exerciseSource.getName())
                .build();
    }
}
