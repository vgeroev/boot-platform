package org.vmalibu.module.exercises.exception;

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

}
