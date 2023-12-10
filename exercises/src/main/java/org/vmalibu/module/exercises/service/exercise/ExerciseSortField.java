package org.vmalibu.module.exercises.service.exercise;

import org.vmalibu.module.exercises.database.domainobject.DbExercise;
import org.vmalibu.modules.database.paging.SortField;

public enum ExerciseSortField implements SortField {

    CREATED_AT(DbExercise.Fields.createdAt),
    UPDATED_AT(DbExercise.Fields.updatedAt),
    PROBLEM_NAME(DbExercise.Fields.problemName),
    SOLUTION_STATUS(DbExercise.Fields.solutionStatus);

    private final String name;
    ExerciseSortField(String name) {
        this.name = name;
    }

    @Override
    public String getFieldName() {
        return name;
    }
}
