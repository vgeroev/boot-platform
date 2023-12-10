package org.vmalibu.module.exercises.service.exercisesource;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.vmalibu.module.exercises.database.domainobject.DbExerciseSource;

@JsonTypeName("exercise_source")
@Getter
@EqualsAndHashCode(of = { "id" })
@Builder
@AllArgsConstructor
public class ExerciseSourceDto {

    private final long id;
    private final String name;
    private final String ownerId;

    public static ExerciseSourceDto from(DbExerciseSource exerciseSource) {
        if (exerciseSource == null) {
            return null;
        }

        return ExerciseSourceDto.builder()
                .id(exerciseSource.getId())
                .name(exerciseSource.getName())
                .ownerId(exerciseSource.getOwnerId())
                .build();
    }
}
