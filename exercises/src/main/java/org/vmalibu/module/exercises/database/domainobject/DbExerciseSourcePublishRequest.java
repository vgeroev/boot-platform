package org.vmalibu.module.exercises.database.domainobject;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.DynamicUpdate;
import org.vmalibu.module.exercises.ExercisesModuleConsts;
import org.vmalibu.module.exercises.database.converter.ExerciseSourcePublishRequestStatusConverter;
import org.vmalibu.module.exercises.service.exercisesourcepublishrequest.ExerciseSourcePublishRequestStatus;
import org.vmalibu.modules.database.domainobject.IdentityGeneratedDomainObject;

import java.util.Date;

@Entity
@Table(
        name = ExercisesModuleConsts.DB_PREFIX + "exercise_source_publish_request",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                DbExerciseSourcePublishRequest.FIELD_USER_ID,
                                DbExerciseSourcePublishRequest.FIELD_EXERCISE_SOURCE_ID
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
public class DbExerciseSourcePublishRequest extends IdentityGeneratedDomainObject {

    public static final String FIELD_REQUESTED_AT = "requested_at";
    public static final String FIELD_AGREED_AT = "agreed_at";
    public static final String FIELD_USER_ID = "user_id";
    public static final String FIELD_EXERCISE_SOURCE_ID = "exercise_source_id";
    public static final String FIELD_STATUS = "status";

    @Column(name = FIELD_REQUESTED_AT, nullable = false)
    private Date requestedAt;

    @Column(name = FIELD_AGREED_AT)
    private Date agreedAt;

    @Column(name = FIELD_USER_ID, nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = FIELD_EXERCISE_SOURCE_ID, nullable = false, updatable = false)
    private DbExerciseSource exerciseSource;

    @Column(name = FIELD_STATUS, nullable = false)
    @Convert(converter = ExerciseSourcePublishRequestStatusConverter.class)
    private ExerciseSourcePublishRequestStatus status;
}
