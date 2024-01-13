package org.vmalibu.module.exercises.exception;

import org.vmalibu.module.exercises.service.exercisesourcepublishrequest.ExerciseSourcePublishRequestStatus;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.HashMap;
import java.util.Map;

public class ExercisesExceptionFactory {

    private ExercisesExceptionFactory() { }

    public static PlatformException buildAccessDeniedOnExerciseSourceException(long exerciseSourceId, String userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("exercise_source_id", exerciseSourceId);
        params.put("user_id", userId);
        return new PlatformException("access_denied_on_exercise_source", params);
    }

    public static PlatformException buildExerciseSourceAlreadyPublishedException(long exerciseSourceId) {
        Map<String, Object> params = new HashMap<>();
        params.put("exercise_source_id", exerciseSourceId);
        return new PlatformException("exercise_source_already_published", params);
    }

    public static PlatformException buildInvalidExerciseSourcePublishRequestStatusException(ExerciseSourcePublishRequestStatus status) {
        Map<String, Object> params = new HashMap<>();
        params.put("status", status);
        return new PlatformException("invalid_exercise_source_publish_request_status", params);
    }

}
