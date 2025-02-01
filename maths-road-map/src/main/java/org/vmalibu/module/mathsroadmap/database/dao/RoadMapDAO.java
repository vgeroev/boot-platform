package org.vmalibu.module.mathsroadmap.database.dao;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBRoadMap;
import org.vmalibu.modules.database.repository.PaginatedDomainObjectRepository;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.Optional;

@Repository
public interface RoadMapDAO extends PaginatedDomainObjectRepository<Long, DBRoadMap> {

    @Query("from DBRoadMap r join fetch r.creator where r.id = :id")
    Optional<DBRoadMap> findWithCreator(@Param("id") long id);

    @NonNull
    default DBRoadMap checkExistenceAndGet(long id) throws PlatformException {
        return PaginatedDomainObjectRepository.super.checkExistenceAndGet(id, DBRoadMap.class);
    }

}
