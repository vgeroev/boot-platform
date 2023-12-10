package org.vmalibu.module.exercises.service.exercisesource;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Builder;
import org.vmalibu.module.exercises.service.exercise.ExerciseSolutionStatus;

import java.util.Map;

@JsonTypeName("exercise_source_statistics")
@Builder
public record ExerciseSourceStats(Map<ExerciseSolutionStatus, Integer> solutionStatusesStats) {

}
