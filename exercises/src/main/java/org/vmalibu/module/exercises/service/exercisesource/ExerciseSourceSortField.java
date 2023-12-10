package org.vmalibu.module.exercises.service.exercisesource;

import org.vmalibu.module.exercises.database.domainobject.DbExerciseSource;
import org.vmalibu.module.exercises.database.domainobject.DbExerciseSourceAccess;
import org.vmalibu.modules.database.paging.SortField;

public enum ExerciseSourceSortField implements SortField {

    NAME(getInnerProperty(DbExerciseSourceAccess.Fields.exerciseSource, DbExerciseSource.Fields.name));

    private final String name;
    ExerciseSourceSortField(String name) {
        this.name = name;
    }

    @Override
    public String getFieldName() {
        return name;
    }

    private static String getInnerProperty(String name, String innerName) {
        return "%s.%s".formatted(name, innerName);
    }
}
