package org.vmalibu.module.exercises.service.exercisesourcepublishrequest;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.modules.utils.BaseEnum;

public enum ExerciseSourcePublishRequestStatus implements BaseEnum {

    PENDING(1),
    GRANTED(2),
    DENIED(3);

    private final int id;
    ExerciseSourcePublishRequestStatus(int id) {
        this.id = id;
    }

    @Override
    public int intValue() {
        return id;
    }

    public static @Nullable ExerciseSourcePublishRequestStatus from(@Nullable Integer value) {
        if (value == null) {
            return null;
        }

        for (ExerciseSourcePublishRequestStatus status : values()) {
            if (status.id == value) {
                return status;
            }
        }

        throw new IllegalArgumentException("Invalid key=" + value);
    }
}
