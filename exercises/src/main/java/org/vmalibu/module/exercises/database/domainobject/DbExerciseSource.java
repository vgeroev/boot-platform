package org.vmalibu.module.exercises.database.domainobject;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.DynamicUpdate;
import org.vmalibu.module.exercises.ExercisesModuleConsts;
import org.vmalibu.modules.database.domainobject.DomainObject;

@Entity
@Table(
        name = ExercisesModuleConsts.DB_PREFIX + "exercise_source",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                DbExerciseSource.FIELD_NAME,
                                DbExerciseSource.FIELD_OWNER_ID
                        }
                )
        }
)
@DynamicUpdate
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
public class DbExerciseSource extends DomainObject {

    public static final String FIELD_NAME = "name";
    public static final String FIELD_OWNER_ID = "owner_id";

    @Column(name = FIELD_NAME, nullable = false)
    private String name;

    @Column(name = FIELD_OWNER_ID, nullable = false)
    private String ownerId;

}
