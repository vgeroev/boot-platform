package org.vmalibu.module.exercises.service.exercisesourcepublishrequest;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.vmalibu.modules.module.exception.PlatformException;

public interface ExerciseSourcePublishRequestService {

    @Transactional
    @NonNull ExerciseSourcePublishRequestDto createRequest(
            @NonNull ExerciseSourcePublishRequestCreationBuilder builder) throws PlatformException;

    @Transactional
    @NonNull ExerciseSourcePublishRequestDto agreeOnRequest(
            long exerciseSourceId,
            @NonNull ExerciseSourcePublishRequestStatus status) throws PlatformException;


}
