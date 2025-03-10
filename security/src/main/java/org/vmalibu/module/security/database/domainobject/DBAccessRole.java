package org.vmalibu.module.security.database.domainobject;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.vmalibu.module.security.SecurityModuleConsts;
import org.vmalibu.module.security.access.struct.AccessOp;
import org.vmalibu.modules.database.domainobject.IdentityGeneratedDomainObject;

import java.util.HashMap;
import java.util.Map;
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
    public static final String DB_ADMIN = "admin";

    @Column(name = DB_NAME, nullable = false)
    private String name;

    @Column(name = DB_ADMIN, nullable = false)
    private boolean admin;

    @ElementCollection
    @CollectionTable(
            name = SecurityModuleConsts.DB_PREFIX + "privilege",
            joinColumns = @JoinColumn(name = "fk_access_role")
    )
    @MapKeyColumn(name = DBPrivilege.DB_KEY)
    @Column(name = DBPrivilege.DB_VALUE)
    private Map<String, Set<AccessOp>> privileges = new HashMap<>();

}
