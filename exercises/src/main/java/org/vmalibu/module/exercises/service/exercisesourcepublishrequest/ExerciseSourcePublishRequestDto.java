package org.vmalibu.module.exercises.service.exercisesourcepublishrequest;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Builder;
import org.vmalibu.module.exercises.database.domainobject.DbExerciseSourcePublishRequest;

import java.util.Date;

@JsonTypeName("exercise_source_publish_request")
@Builder
public record ExerciseSourcePublishRequestDto(
        Date requestedAt,
        Date agreedAt,
        String userId,
        long exerciseSourceId,
        ExerciseSourcePublishRequestStatus status
) {

    public static ExerciseSourcePublishRequestDto from(DbExerciseSourcePublishRequest exerciseSourcePublishRequest) {
        if (exerciseSourcePublishRequest == null) {
            return null;
        }

        return ExerciseSourcePublishRequestDto.builder()
                .requestedAt(exerciseSourcePublishRequest.getRequestedAt())
                .agreedAt(exerciseSourcePublishRequest.getAgreedAt())
                .userId(exerciseSourcePublishRequest.getUserId())
                .exerciseSourceId(exerciseSourcePublishRequest.getExerciseSource().getId())
                .status(exerciseSourcePublishRequest.getStatus())
                .build();
    }
}
