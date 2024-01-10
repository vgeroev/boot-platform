package org.vmalibu.module.exercises.service.exercise;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.vmalibu.module.exercises.service.exercise.list.ExerciseListElement;
import org.vmalibu.module.exercises.service.exercise.list.ExercisePagingRequest;
import org.vmalibu.modules.database.paging.PaginatedDto;
import org.vmalibu.modules.module.exception.PlatformException;

public interface ExerciseService {

    @Transactional(readOnly = true)
    @Nullable ExerciseDto get(long id);

    @Transactional
    @NonNull ExerciseDto create(@NonNull ExerciseBuilder builder) throws PlatformException;

    @Transactional
    @NonNull ExerciseDto update(long id, @NonNull ExerciseBuilder builder) throws PlatformException;

    @Transactional
    void delete(long id);

    @Transactional(readOnly = true)
    @NonNull PaginatedDto<ExerciseListElement> findAll(@NonNull ExercisePagingRequest pagingRequest);

}
