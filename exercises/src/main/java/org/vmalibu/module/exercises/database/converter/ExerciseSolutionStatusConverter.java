package org.vmalibu.module.exercises.database.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.vmalibu.module.exercises.service.exercise.ExerciseSolutionStatus;

import java.util.Optional;

@Converter
public class ExerciseSolutionStatusConverter implements AttributeConverter<ExerciseSolutionStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ExerciseSolutionStatus attribute) {
        return Optional.ofNullable(attribute).map(ExerciseSolutionStatus::intValue).orElse(null);
    }

    @Override
    public ExerciseSolutionStatus convertToEntityAttribute(Integer dbData) {
        return ExerciseSolutionStatus.from(dbData);
    }
}
