package org.vmalibu.module.exercises.database.domainobject;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.DynamicUpdate;
import org.vmalibu.module.exercises.ExercisesModuleConsts;
import org.vmalibu.module.security.access.AccessOp;
import org.vmalibu.module.security.database.converter.AccessOpsConverter;
import org.vmalibu.modules.database.domainobject.IdentityGeneratedDomainObject;

import java.util.Set;

@Entity
@Table(
        name = ExercisesModuleConsts.DB_PREFIX + "exercise_source_access",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                DbExerciseSourceAccess.FIELD_USER_ID,
                                DbExerciseSourceAccess.FIELD_EXERCISE_SOURCE_ID
                        }
                )
        }
)
@DynamicUpdate
@Access(value = AccessType.FIELD)
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
public class DbExerciseSourceAccess extends IdentityGeneratedDomainObject {

    public static final String FIELD_USER_ID = "user_id";
    public static final String FIELD_ACCESS_OPS = "access_ops";
    public static final String FIELD_EXERCISE_SOURCE_ID = "exercise_source_id";

    @Column(name = FIELD_USER_ID, nullable = false)
    private String userId;

    @Column(name = FIELD_ACCESS_OPS, nullable = false)
    @Convert(converter = AccessOpsConverter.class)
    private Set<AccessOp> accessOps;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = FIELD_EXERCISE_SOURCE_ID, nullable = false)
    private DbExerciseSource exerciseSource;

}
