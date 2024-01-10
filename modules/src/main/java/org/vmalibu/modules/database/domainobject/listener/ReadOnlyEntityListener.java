package org.vmalibu.modules.database.domainobject.listener;

import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import org.vmalibu.modules.database.domainobject.DomainObject;

public class ReadOnlyEntityListener {

    @PrePersist
    @PreUpdate
    @PreRemove
    @PostLoad
    private void fireException(DomainObject domainObject) {
        throw new UnsupportedOperationException("Domain object (name: %s) is readonly".formatted(domainObject.getClass()));
    }
}
