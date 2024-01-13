package org.vmalibu.module.exercises.database.converter;

import org.vmalibu.module.exercises.service.exercise.ExerciseSolutionStatus;
import org.vmalibu.modules.database.converter.BaseEnumConverter;

public class ExerciseSolutionStatusConverter extends BaseEnumConverter<ExerciseSolutionStatus> {

    public ExerciseSolutionStatusConverter() {
        super(ExerciseSolutionStatus::from);
    }
}
