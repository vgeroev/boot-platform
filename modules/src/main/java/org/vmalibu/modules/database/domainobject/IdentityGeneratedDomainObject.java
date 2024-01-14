package org.vmalibu.modules.database.domainobject;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class IdentityGeneratedDomainObject extends VersionedDomainObject {

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return super.getId();
    }
}
