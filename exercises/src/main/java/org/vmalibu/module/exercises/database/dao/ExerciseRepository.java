package org.vmalibu.module.exercises.database.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vmalibu.module.exercises.database.domainobject.DbExercise;
import org.vmalibu.modules.database.repository.PaginatedDomainObjectRepository;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.Optional;

@Repository
public interface ExerciseRepository extends PaginatedDomainObjectRepository<DbExercise> {

    DbExercise findByProblemNameAndExerciseSourceId(String problemName, long exerciseSourceId);

    @Query("select e from DbExercise e join fetch e.exerciseSource where e.id = :id")
    Optional<DbExercise> findWithExerciseSource(@Param("id") long id);

    default DbExercise checkExistenceAndGet(long id) throws PlatformException {
        return PaginatedDomainObjectRepository.super.checkExistenceAndGet(id, DbExercise.class);
    }
}
