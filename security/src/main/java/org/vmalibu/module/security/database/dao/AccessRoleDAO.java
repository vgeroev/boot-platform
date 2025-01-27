package org.vmalibu.module.security.database.dao;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vmalibu.module.security.database.domainobject.DBAccessRole;
import org.vmalibu.modules.database.repository.PaginatedDomainObjectRepository;
import org.vmalibu.modules.module.exception.PlatformException;

@Repository
public interface AccessRoleDAO extends PaginatedDomainObjectRepository<DBAccessRole> {

    @Query("select count(a.id) > 0 from DBAccessRole a where a.name = :name")
    boolean isExistByName(@Param("name") String name);

    @NonNull
    default DBAccessRole checkExistenceAndGet(long id) throws PlatformException {
        return PaginatedDomainObjectRepository.super.checkExistenceAndGet(id, DBAccessRole.class);
    }
}
