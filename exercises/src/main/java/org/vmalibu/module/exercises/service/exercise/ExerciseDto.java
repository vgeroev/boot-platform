package org.vmalibu.module.exercises.service.exercise;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Builder;
import org.vmalibu.module.exercises.database.domainobject.DbExercise;

import java.util.Date;

@JsonTypeName("exercise")
@Builder
public record ExerciseDto(long id,
                          Date createdAt,
                          Date updatedAt,
                          String problemName,
                          String problem,
                          ExerciseSolutionStatus solutionStatus,
                          String solution,
                          long exerciseSourceId) {

    public static ExerciseDto from(DbExercise exercise) {
        if (exercise == null) {
            return null;
        }

        return ExerciseDto.builder()
                .id(exercise.getId())
                .createdAt(exercise.getCreatedAt())
                .updatedAt(exercise.getUpdatedAt())
                .problemName(exercise.getProblemName())
                .problem(exercise.getProblem())
                .solutionStatus(exercise.getSolutionStatus())
                .solution(exercise.getSolution())
                .exerciseSourceId(exercise.getExerciseSource().getId())
                .build();
    }

}
