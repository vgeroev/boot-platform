package org.vmalibu.module.exercises.service.exercisesourceaccess;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.vmalibu.module.security.access.AccessOp;
import org.vmalibu.modules.module.exception.PlatformException;

public interface ExerciseSourceAccessService {

    @Transactional(readOnly = true)
    boolean hasAccess(@NonNull String userId, long exerciseSourceId, @NonNull AccessOp... accessOps) throws PlatformException;

    @Transactional
    void setAccesses(@NonNull String userId, long exerciseSourceId, @NonNull AccessOp... accessOps) throws PlatformException;

}
