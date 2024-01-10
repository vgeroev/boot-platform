package org.vmalibu.module.exercises.database.dao;

import org.springframework.stereotype.Repository;
import org.vmalibu.module.exercises.database.domainobject.DbExerciseSource;
import org.vmalibu.modules.database.repository.PaginatedDomainObjectRepository;
import org.vmalibu.modules.module.exception.PlatformException;

@Repository
public interface ExerciseSourceRepository extends PaginatedDomainObjectRepository<DbExerciseSource> {

    DbExerciseSource findByNameAndOwnerId(String name, String ownerId);

    default DbExerciseSource checkExistenceAndGet(long id) throws PlatformException {
        return PaginatedDomainObjectRepository.super.checkExistenceAndGet(id, DbExerciseSource.class);
    }
}
