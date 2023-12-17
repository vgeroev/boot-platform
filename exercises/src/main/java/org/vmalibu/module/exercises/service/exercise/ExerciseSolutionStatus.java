package org.vmalibu.module.exercises.service.exercise;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.modules.utils.BaseEnum;

public enum ExerciseSolutionStatus implements BaseEnum {

    UNSOLVED(1),
    SOLVED(2),
    UNSURE(3),
    PARTIALLY_SOLVED(4);

    private final int id;
    ExerciseSolutionStatus(int id) {
        this.id = id;
    }

    @Override
    public int intValue() {
        return id;
    }

    public static @Nullable ExerciseSolutionStatus from(@Nullable Integer value) {
        if (value == null) {
            return null;
        }

        for (ExerciseSolutionStatus status : values()) {
            if (status.id == value) {
                return status;
            }
        }

        throw new IllegalArgumentException("Invalid key=" + value);
    }
}
