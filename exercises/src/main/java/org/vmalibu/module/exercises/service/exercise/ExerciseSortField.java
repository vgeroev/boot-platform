package org.vmalibu.module.exercises.service.exercise;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.data.domain.Sort;
import org.vmalibu.module.exercises.database.domainobject.DbExercise;
import org.vmalibu.modules.database.paging.SortField;

public enum ExerciseSortField implements SortField {

    CREATED_AT(Sort.by(DbExercise.Fields.createdAt)),
    UPDATED_AT(Sort.by(DbExercise.Fields.updatedAt)),
    PROBLEM_NAME(Sort.by(DbExercise.Fields.problemName)),
    SOLUTION_STATUS(Sort.by(DbExercise.Fields.solutionStatus));

    private final Sort sort;
    ExerciseSortField(Sort sort) {
        this.sort = sort;
    }

    @Override
    public @NonNull Sort getSort() {
        return sort;
    }
}
