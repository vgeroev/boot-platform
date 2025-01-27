package org.vmalibu.module.security.database.domainobject;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.vmalibu.module.security.SecurityModuleConsts;
import org.vmalibu.modules.database.domainobject.IdentityGeneratedDomainObject;

import java.util.HashSet;
import java.util.Set;

@Table(
        name = SecurityModuleConsts.DB_PREFIX + "access_role"
)
@Entity
@Getter
@Setter
@Access(value = AccessType.FIELD)
@NoArgsConstructor
@FieldNameConstants
public class DBAccessRole extends IdentityGeneratedDomainObject {

    public static final String DB_NAME = "name";

    @Column(name = DB_NAME, nullable = false)
    private String name;

    @ElementCollection
    @CollectionTable(
            name = SecurityModuleConsts.DB_PREFIX + "privilege",
            joinColumns = @JoinColumn(name = "fk_access_role")
    )
    private Set<DBPrivilege> privileges = new HashSet<>();

}
