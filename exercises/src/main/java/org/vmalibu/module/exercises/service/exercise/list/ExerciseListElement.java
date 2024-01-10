package org.vmalibu.module.exercises.service.exercise.list;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Builder;
import org.vmalibu.module.exercises.database.domainobject.DbExerciseListElement;
import org.vmalibu.module.exercises.service.exercise.ExerciseSolutionStatus;

import java.util.Date;

@JsonTypeName("exercise_list_element")
@Builder
public record ExerciseListElement(long id,
                                  Date createdAt,
                                  Date updatedAt,
                                  String problemName,
                                  ExerciseSolutionStatus solutionStatus) {

    public static ExerciseListElement from(DbExerciseListElement exercise) {
        if (exercise == null) {
            return null;
        }

        return ExerciseListElement.builder()
                .id(exercise.getId())
                .createdAt(exercise.getCreatedAt())
                .updatedAt(exercise.getUpdatedAt())
                .problemName(exercise.getProblemName())
                .solutionStatus(exercise.getSolutionStatus())
                .build();
    }
}
