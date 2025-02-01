package org.vmalibu.modules.database.domainobject;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class IdentityGeneratedDomainObject extends VersionedDomainObject<Long> {

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return super.getId();
    }
}
