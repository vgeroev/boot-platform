package org.vmalibu.module.exercises.database.dao;

import org.springframework.stereotype.Repository;
import org.vmalibu.module.exercises.database.domainobject.DbExercise;
import org.vmalibu.modules.database.repository.PaginatedDomainObjectRepository;
import org.vmalibu.modules.module.exception.PlatformException;

@Repository
public interface ExerciseRepository extends PaginatedDomainObjectRepository<DbExercise> {

    DbExercise findByProblemNameAndExerciseSourceId(String problemName, long exerciseSourceId);

    default DbExercise checkExistenceAndGet(long id) throws PlatformException {
        return PaginatedDomainObjectRepository.super.checkExistenceAndGet(id, DbExercise.class);
    }
}
