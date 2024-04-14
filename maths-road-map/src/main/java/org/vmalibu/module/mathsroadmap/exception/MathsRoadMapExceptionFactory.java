package org.vmalibu.module.mathsroadmap.exception;

import org.vmalibu.modules.module.exception.PlatformException;

import java.util.Map;

public class MathsRoadMapExceptionFactory {

    public static final String TOPICS_HAVE_CYCLE_CODE = "topics_have_cycle";
    public static final String INVALID_TOPIC_EASINESS_LEVEL_CODE = "invalid_topic_easiness_level";

    private MathsRoadMapExceptionFactory() { }

    public static PlatformException buildTopicsHaveCycleException() {
        return new PlatformException(TOPICS_HAVE_CYCLE_CODE);
    }

    public static PlatformException buildInvalidTopicEasinessLevelException(int easinessLevel) {
        return new PlatformException(INVALID_TOPIC_EASINESS_LEVEL_CODE, Map.of("easinessLevel", easinessLevel));
    }
}
