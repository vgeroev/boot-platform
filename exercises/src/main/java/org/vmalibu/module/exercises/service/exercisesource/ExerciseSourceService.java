package org.vmalibu.module.exercises.service.exercisesource;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.vmalibu.module.security.access.AccessOp;
import org.vmalibu.modules.database.paging.PaginatedDto;
import org.vmalibu.modules.database.paging.SortDirection;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.utils.OptionalField;

import java.util.Set;

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
    @NonNull PaginatedDto<ExerciseSourceListElement> findAll(int page,
                                                             int pageSize,
                                                             @Nullable ExerciseSourceSortField sortField,
                                                             @Nullable SortDirection direction,
                                                             @NonNull String userIdFilter,
                                                             @NonNull OptionalField<Set<AccessOp>> accessOpsFilter,
                                                             @NonNull OptionalField<String> nameFilter);

    @Transactional(readOnly = true)
    @NonNull PaginatedDto<ExerciseSourceListElement> findAll(@Nullable Integer limit,
                                                             @Nullable ExerciseSourceSortField sortField,
                                                             @Nullable SortDirection direction,
                                                             @NonNull String userIdFilter,
                                                             @NonNull OptionalField<Set<AccessOp>> accessOpsFilter,
                                                             @NonNull OptionalField<String> nameFilter);


}
