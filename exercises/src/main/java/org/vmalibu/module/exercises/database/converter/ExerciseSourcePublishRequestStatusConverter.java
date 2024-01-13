package org.vmalibu.module.exercises.database.converter;

import org.vmalibu.module.exercises.service.exercisesourcepublishrequest.ExerciseSourcePublishRequestStatus;
import org.vmalibu.modules.database.converter.BaseEnumConverter;

public class ExerciseSourcePublishRequestStatusConverter extends BaseEnumConverter<ExerciseSourcePublishRequestStatus> {

    public ExerciseSourcePublishRequestStatusConverter() {
        super(ExerciseSourcePublishRequestStatus::from);
    }
}
