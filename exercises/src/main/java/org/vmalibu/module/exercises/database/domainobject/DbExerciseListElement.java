package org.vmalibu.module.exercises.database.domainobject;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.vmalibu.module.exercises.ExercisesModuleConsts;
import org.vmalibu.module.exercises.database.converter.ExerciseSolutionStatusConverter;
import org.vmalibu.module.exercises.service.exercise.ExerciseSolutionStatus;
import org.vmalibu.modules.database.domainobject.DomainObject;
import org.vmalibu.modules.database.domainobject.listener.ReadOnlyEntityListener;

import java.util.Date;

@Entity
@Table(
        name = ExercisesModuleConsts.DB_PREFIX + "exercise"
)
@EntityListeners(ReadOnlyEntityListener.class)
@Getter
@Setter(value = AccessLevel.PROTECTED)
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
public class DbExerciseListElement extends DomainObject {

    @Column(name = DbExercise.CREATED_AT)
    private Date createdAt;

    @Column(name = DbExercise.UPDATED_AT)
    private Date updatedAt;

    @Column(name = DbExercise.FIELD_PROBLEM_NAME)
    private String problemName;

    @Column(name = DbExercise.FIELD_SOLUTION_STATUS)
    @Convert(converter = ExerciseSolutionStatusConverter.class)
    private ExerciseSolutionStatus solutionStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = DbExercise.FIELD_EXERCISE_SOURCE_ID)
    private DbExerciseSource exerciseSource;

}
