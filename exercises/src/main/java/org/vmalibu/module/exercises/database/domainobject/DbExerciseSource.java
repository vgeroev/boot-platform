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
import org.vmalibu.modules.database.domainobject.DomainObject;

import java.util.Date;
import java.util.List;

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
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
public class DbExerciseSource extends DomainObject {

    public static final String FIELD_NAME = "name";
    public static final String FIELD_OWNER_ID = "owner_id";
    public static final String FIELD_PUBLISHED = "published";

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = FIELD_NAME, nullable = false)
    private String name;

    @Column(name = FIELD_OWNER_ID, nullable = false)
    private String ownerId;

    @Column(name = FIELD_PUBLISHED, nullable = false)
    private boolean published;

    // Necessary only for filtering by criteria
    @OneToMany(mappedBy = DbExerciseSourceAccess.Fields.exerciseSource)
    private List<DbExerciseSourceAccess> exerciseSourceAccesses;

    protected List<DbExerciseSourceAccess> getExerciseSourceAccesses() {
        return exerciseSourceAccesses;
    }

    protected void setExerciseSourceAccesses(List<DbExerciseSourceAccess> exerciseSourceAccesses) {
        this.exerciseSourceAccesses = exerciseSourceAccesses;
    }
}
