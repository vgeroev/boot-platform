package org.vmalibu.module.exercises.service.exercisesource.list;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.data.domain.Sort;
import org.vmalibu.module.exercises.database.domainobject.DbExerciseSource;
import org.vmalibu.modules.database.paging.SortField;

public enum ExerciseSourceSortField implements SortField {

    CREATED_AT(Sort.by(DbExerciseSource.Fields.createdAt)),
    UPDATED_AT(Sort.by(DbExerciseSource.Fields.updatedAt));

    private final Sort sort;
    ExerciseSourceSortField(Sort sort) {
        this.sort = sort;
    }

    @Override
    public @NonNull Sort getSort() {
        return sort;
    }


}
