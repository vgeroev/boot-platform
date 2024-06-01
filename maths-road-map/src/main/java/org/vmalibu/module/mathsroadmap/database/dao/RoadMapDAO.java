package org.vmalibu.module.mathsroadmap.database.dao;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Repository;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBRoadMap;
import org.vmalibu.modules.database.repository.PaginatedDomainObjectRepository;
import org.vmalibu.modules.module.exception.PlatformException;

@Repository
public interface RoadMapDAO extends PaginatedDomainObjectRepository<DBRoadMap> {

    @NonNull
    default DBRoadMap checkExistenceAndGet(long id) throws PlatformException {
        return PaginatedDomainObjectRepository.super.checkExistenceAndGet(id, DBRoadMap.class);
    }

}
