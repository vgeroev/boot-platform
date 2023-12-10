package org.vmalibu.module.exercises.service.exercisesource;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Builder;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.module.exercises.database.domainobject.DbExerciseSourceAccess;
import org.vmalibu.module.security.access.AccessOp;

import java.util.Set;

@JsonTypeName("exercise_source_list_element")
@Builder
public record ExerciseSourceListElement(Set<AccessOp> accessOps,
                                        ExerciseSourceStats stats,
                                        ExerciseSourceDto exerciseSource) {

    public static @Nullable ExerciseSourceListElement from(@Nullable DbExerciseSourceAccess exerciseSourceAccess) {
        if (exerciseSourceAccess == null) {
            return null;
        }

        return ExerciseSourceListElement.builder()
                .accessOps(exerciseSourceAccess.getAccessOps())
                .exerciseSource(ExerciseSourceDto.from(exerciseSourceAccess.getExerciseSource()))
                .build();
    }
}
