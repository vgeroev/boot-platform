package org.vmalibu.module.exercises.service.exercisesourceaccess;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Service;
import org.vmalibu.module.exercises.database.dao.ExerciseSourceAccessRepository;
import org.vmalibu.module.exercises.database.dao.ExerciseSourceRepository;
import org.vmalibu.module.exercises.database.domainobject.DbExerciseSource;
import org.vmalibu.module.exercises.database.domainobject.DbExerciseSourceAccess;
import org.vmalibu.module.security.access.AccessOp;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.Optional;
import java.util.Set;

@Service
class ExerciseSourceAccessServiceImpl implements ExerciseSourceAccessService {

    private final ExerciseSourceAccessRepository exerciseSourceAccessRepository;
    private final ExerciseSourceRepository exerciseSourceRepository;

    public ExerciseSourceAccessServiceImpl(@NonNull ExerciseSourceAccessRepository exerciseSourceAccessRepository,
                                           @NonNull ExerciseSourceRepository exerciseSourceRepository) {
        this.exerciseSourceAccessRepository = exerciseSourceAccessRepository;
        this.exerciseSourceRepository = exerciseSourceRepository;
    }

    @Override
    public boolean hasAccess(@NonNull String userId, long exerciseSourceId, @NonNull AccessOp... accessOps) {
        DbExerciseSourceAccess sourceAccess = exerciseSourceAccessRepository.findByUserIdAndSourceId(userId, exerciseSourceId);
        if (sourceAccess == null) {
            return false;
        }

        return sourceAccess.getAccessOps().containsAll(Set.of(accessOps));
    }

    @Override
    public void setAccesses(@NonNull String userId, long exerciseSourceId, @NonNull AccessOp... accessOps) throws PlatformException {
        DbExerciseSourceAccess sourceAccess = exerciseSourceAccessRepository.findByUserIdAndSourceId(userId, exerciseSourceId);
        DbExerciseSourceAccess newSourceAccess;
        if (sourceAccess == null) {
            Optional<DbExerciseSource> exerciseSourceOptional = exerciseSourceRepository.findById(exerciseSourceId);
            if (exerciseSourceOptional.isEmpty()) {
                throw GeneralExceptionFactory.buildNotFoundDomainObjectException(DbExerciseSource.class, exerciseSourceId);
            }

            newSourceAccess = new DbExerciseSourceAccess();
            newSourceAccess.setUserId(userId);
            newSourceAccess.setExerciseSource(exerciseSourceOptional.get());
        } else {
            newSourceAccess = sourceAccess;
        }

        newSourceAccess.setAccessOps(Set.of(accessOps));
        exerciseSourceAccessRepository.save(newSourceAccess);
    }


}
