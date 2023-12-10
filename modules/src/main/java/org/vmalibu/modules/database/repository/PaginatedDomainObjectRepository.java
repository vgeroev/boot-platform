package org.vmalibu.modules.database.repository;

import org.vmalibu.modules.database.domainobject.DomainObject;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface PaginatedDomainObjectRepository<T extends DomainObject> extends DomainObjectRepository<T>,
        JpaSpecificationExecutor<T> {
}
