package org.vmalibu.module.exercises.service.exercisesource;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.vmalibu.module.exercises.service.exercisesource.list.ExerciseSourceListElement;
import org.vmalibu.module.exercises.service.exercisesource.list.ExerciseSourcePagingRequest;
import org.vmalibu.modules.database.paging.PaginatedDto;
import org.vmalibu.modules.module.exception.PlatformException;

public interface ExerciseSourceService {

    @Transactional(readOnly = true)
    @Nullable ExerciseSourceDto get(long id);

    @Transactional
    @NonNull ExerciseSourceDto create(@NonNull ExerciseSourceBuilder builder) throws PlatformException;

    @Transactional
    @NonNull ExerciseSourceDto update(long id, @NonNull ExerciseSourceBuilder builder) throws PlatformException;

    @Transactional
    void delete(long id);

    @Transactional(readOnly = true)
    @NonNull PaginatedDto<ExerciseSourceListElement> findAll(@NonNull ExerciseSourcePagingRequest pagingRequest);

}
