package org.vmalibu.module.exercises.service.exercise;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.vmalibu.module.exercises.service.exercisesource.ExerciseSourceDto;
import org.vmalibu.modules.database.paging.PaginatedDto;
import org.vmalibu.modules.database.paging.SortDirection;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.utils.OptionalField;

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
    @Nullable ExerciseSourceDto getExerciseSource(long exerciseId);

    @Transactional(readOnly = true)
    @NonNull PaginatedDto<ExerciseListElement> findAll(int page,
                                                       int pageSize,
                                                       @Nullable ExerciseSortField sortField,
                                                       @Nullable SortDirection direction,
                                                       @NonNull OptionalField<Long> exerciseSourceId,
                                                       @NonNull OptionalField<String> problemNameFilter,
                                                       @NonNull OptionalField<ExerciseSolutionStatus> solutionStatusFilter);

    @Transactional(readOnly = true)
    @NonNull PaginatedDto<ExerciseListElement> findAll(@Nullable Integer limit,
                                                       @Nullable ExerciseSortField sortField,
                                                       @Nullable SortDirection direction,
                                                       @NonNull OptionalField<Long> exerciseSourceId,
                                                       @NonNull OptionalField<String> problemNameFilter,
                                                       @NonNull OptionalField<ExerciseSolutionStatus> solutionStatusFilter);
}
