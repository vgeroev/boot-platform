package org.vmalibu.module.exercises.service.exercise;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.vmalibu.module.exercises.database.domainobject.DbExercise;

import java.util.Date;

@JsonTypeName("exercise")
@Getter
@EqualsAndHashCode(of = { "id" })
@Builder
@AllArgsConstructor
public class ExerciseDto {

    private final long id;
    private final Date createdAt;
    private final Date updatedAt;
    private final String problemName;
    private final String problem;
    private final ExerciseSolutionStatus solutionStatus;
    private final String solution;
    private final long exerciseSourceId;

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
