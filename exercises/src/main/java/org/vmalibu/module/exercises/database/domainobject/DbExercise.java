package org.vmalibu.module.exercises.database.domainobject;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.vmalibu.module.exercises.ExercisesModuleConsts;
import org.vmalibu.module.exercises.database.converter.ExerciseSolutionStatusConverter;
import org.vmalibu.module.exercises.service.exercise.ExerciseSolutionStatus;
import org.vmalibu.modules.database.domainobject.DomainObject;

import java.util.Date;

@Entity
@Table(
        name = ExercisesModuleConsts.DB_PREFIX + "exercise",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                DbExercise.FIELD_PROBLEM_NAME,
                                DbExercise.FIELD_EXERCISE_SOURCE_ID
                        }
                )
        }
)
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
public class DbExercise extends DomainObject {

    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";
    public static final String FIELD_PROBLEM_NAME = "problem_name";
    public static final String FIELD_PROBLEM = "problem";
    public static final String FIELD_SOLUTION_STATUS = "solution_status";
    public static final String FIELD_SOLUTION = "solution";
    public static final String FIELD_EXERCISE_SOURCE_ID = "exercise_source_id";

    @CreatedDate
    @Column(name = CREATED_AT, nullable = false, updatable = false)
    private Date createdAt;

    @LastModifiedDate
    @Column(name = UPDATED_AT)
    private Date updatedAt;

    @Column(name = FIELD_PROBLEM_NAME, nullable = false)
    private String problemName;

    @Column(name = FIELD_PROBLEM, nullable = false)
    @Lob
    private String problem;

    @Column(name = FIELD_SOLUTION_STATUS, nullable = false)
    @Convert(converter = ExerciseSolutionStatusConverter.class)
    private ExerciseSolutionStatus solutionStatus;

    @Column(name = FIELD_SOLUTION)
    @Lob
    private String solution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = FIELD_EXERCISE_SOURCE_ID, nullable = false)
    private DbExerciseSource exerciseSource;
}
