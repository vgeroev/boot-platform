package org.vmalibu.module.exercises.service.exercise;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Builder;
import org.vmalibu.module.exercises.database.domainobject.DbExercise;

import java.util.Date;

@JsonTypeName("exercise_list_element")
@Builder
public record ExerciseListElement(long id,
                                  Date createdAt,
                                  Date updatedAt,
                                  String problemName,
                                  String problem,
                                  ExerciseSolutionStatus solutionStatus) {

    public static ExerciseListElement from(DbExercise exercise) {
        if (exercise == null) {
            return null;
        }

        return ExerciseListElement.builder()
                .id(exercise.getId())
                .createdAt(exercise.getCreatedAt())
                .updatedAt(exercise.getUpdatedAt())
                .problemName(exercise.getProblemName())
                .problem(exercise.getProblem())
                .solutionStatus(exercise.getSolutionStatus())
                .build();
    }
}
