package org.vmalibu.modules.database.repository;

import org.vmalibu.modules.database.domainobject.DomainObject;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface PaginatedDomainObjectRepository<I, T extends DomainObject<I>> extends DomainObjectRepository<I, T>,
        JpaSpecificationExecutor<T> {
}
