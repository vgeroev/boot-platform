package org.vmalibu.module.exercises.service.exercisesourcepublishrequest;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.vmalibu.module.exercises.database.dao.ExerciseSourcePublishRequestRepository;
import org.vmalibu.module.exercises.database.dao.ExerciseSourceRepository;
import org.vmalibu.module.exercises.database.domainobject.DbExerciseSource;
import org.vmalibu.module.exercises.database.domainobject.DbExerciseSourcePublishRequest;
import org.vmalibu.module.exercises.exception.ExercisesExceptionFactory;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.Date;
import java.util.Objects;

@Service
class ExerciseSourcePublishRequestServiceImpl implements ExerciseSourcePublishRequestService {

    private final ExerciseSourcePublishRequestRepository exerciseSourcePublishRequestRepository;
    private final ExerciseSourceRepository exerciseSourceRepository;

    public ExerciseSourcePublishRequestServiceImpl(ExerciseSourcePublishRequestRepository exerciseSourcePublishRequestRepository,
                                                   ExerciseSourceRepository exerciseSourceRepository) {
        this.exerciseSourcePublishRequestRepository = exerciseSourcePublishRequestRepository;
        this.exerciseSourceRepository = exerciseSourceRepository;
    }

    @Override
    public @NonNull ExerciseSourcePublishRequestDto createRequest(
            @NonNull ExerciseSourcePublishRequestCreationBuilder builder) throws PlatformException {
        Long exerciseSourceId = builder.getExerciseSourceId();
        if (!builder.isContainExerciseSourceId() || exerciseSourceId == null) {
            throw GeneralExceptionFactory.buildEmptyValueException(
                    DbExerciseSourcePublishRequest.class, DbExerciseSourcePublishRequest.Fields.exerciseSource);
        }

        if (!builder.isContainUserId() || !StringUtils.hasText(builder.getUserId())) {
            throw GeneralExceptionFactory.buildEmptyValueException(
                    DbExerciseSourcePublishRequest.class, DbExerciseSourcePublishRequest.Fields.userId);
        }

        DbExerciseSource exerciseSource = exerciseSourceRepository.checkExistenceAndGet(exerciseSourceId);
        checkExerciseSource(exerciseSource, builder);

        DbExerciseSourcePublishRequest publishRequest = exerciseSourcePublishRequestRepository.findByExerciseSourceId(
                exerciseSourceId
        );
        if (publishRequest == null) {
            publishRequest = new DbExerciseSourcePublishRequest();
        }

        setFieldsOnCreation(publishRequest, exerciseSource, builder);
        return ExerciseSourcePublishRequestDto.from(exerciseSourcePublishRequestRepository.save(publishRequest));
    }

    @Override
    public @NonNull ExerciseSourcePublishRequestDto agreeOnRequest(
            long exerciseSourceId,
            @NonNull ExerciseSourcePublishRequestStatus status
    ) throws PlatformException {
        Objects.requireNonNull(status);
        if (status == ExerciseSourcePublishRequestStatus.PENDING) {
            throw ExercisesExceptionFactory.buildInvalidExerciseSourcePublishRequestStatusException(status);
        }

        DbExerciseSourcePublishRequest publishRequest = exerciseSourcePublishRequestRepository.findByExerciseSourceId(
                exerciseSourceId
        );
        if (publishRequest == null) {
            throw GeneralExceptionFactory.buildNotFoundDomainObjectException(
                    DbExerciseSourcePublishRequest.class,
                    DbExerciseSourcePublishRequest.Fields.exerciseSource,
                    exerciseSourceId
            );
        }

        if (status != publishRequest.getStatus()) {
            publishRequest.setAgreedAt(new Date());
            publishRequest.setStatus(status);
            publishRequest = exerciseSourcePublishRequestRepository.save(publishRequest);
            updateExerciseSource(exerciseSourceId, status);
        }

        return ExerciseSourcePublishRequestDto.from(publishRequest);
    }

    private void checkExerciseSource(DbExerciseSource exerciseSource,
                                     ExerciseSourcePublishRequestCreationBuilder builder) throws PlatformException {
        if (exerciseSource.isPublished()) {
            throw ExercisesExceptionFactory.buildExerciseSourceAlreadyPublishedException(exerciseSource.getId());
        }

        String userId = builder.getUserId();
        if (!Objects.equals(exerciseSource.getOwnerId(), userId)) {
            throw ExercisesExceptionFactory.buildAccessDeniedOnExerciseSourceException(exerciseSource.getId(), userId);
        }
    }

    private void setFieldsOnCreation(
            DbExerciseSourcePublishRequest exerciseSourcePublishRequest,
            DbExerciseSource exerciseSource,
            ExerciseSourcePublishRequestCreationBuilder builder
    ) {
        exerciseSourcePublishRequest.setRequestedAt(new Date());
        exerciseSourcePublishRequest.setAgreedAt(null);
        exerciseSourcePublishRequest.setUserId(builder.getUserId());
        exerciseSourcePublishRequest.setExerciseSource(exerciseSource);
        exerciseSourcePublishRequest.setStatus(ExerciseSourcePublishRequestStatus.PENDING);
    }

    private void updateExerciseSource(long exerciseSourceId,
                                      ExerciseSourcePublishRequestStatus status) throws PlatformException {
        DbExerciseSource exerciseSource = exerciseSourceRepository.checkExistenceAndGet(exerciseSourceId);
        if (status == ExerciseSourcePublishRequestStatus.GRANTED) {
            exerciseSource.setPublished(true);
        } else if (status == ExerciseSourcePublishRequestStatus.DENIED) {
            exerciseSource.setPublished(false);
        } else {
            throw new IllegalStateException("Unknown status: " + status);
        }

        exerciseSourceRepository.save(exerciseSource);
    }
}
