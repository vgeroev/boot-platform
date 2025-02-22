package org.vmalibu.module.core.database.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vmalibu.module.core.database.domainobject.DBTag;
import org.vmalibu.modules.database.repository.PaginatedDomainObjectRepository;

@Repository
public interface TagDAO extends PaginatedDomainObjectRepository<Long, DBTag> {

    @Query("select count(*) > 0 from DBTag t where t.name = :name")
    boolean isExistByName(@Param("name") String name);
}
