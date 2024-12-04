package org.vmalibu.module.security.database.domainobject;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.vmalibu.module.security.SecurityModuleConsts;
import org.vmalibu.modules.database.domainobject.IdentityGeneratedDomainObject;

import java.util.Date;

@Table(
        name = SecurityModuleConsts.DB_PREFIX + "users"
)
@Entity
@Getter
@Setter
@Access(value = AccessType.FIELD)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@FieldNameConstants
public class DBUser extends IdentityGeneratedDomainObject {

    public static final String DB_USERNAME = "username";
    public static final String DB_PASSWORD = "password";
    public static final String DB_CREATED_AT = "created_at";
    public static final String DB_UPDATED_AT = "updated_at";

    @Column(name = DB_USERNAME, nullable = false)
    private String username;

    @Column(name = DB_PASSWORD, nullable = false)
    private String password;

    @CreatedDate
    @Column(name = DB_CREATED_AT, nullable = false)
    private Date createdAt;

    @LastModifiedDate
    @Column(name = DB_UPDATED_AT)
    private Date updatedAt;
}
