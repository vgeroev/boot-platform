package org.vmalibu.module.exercises.database.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vmalibu.module.exercises.database.domainobject.DbExerciseSourceAccess;
import org.vmalibu.modules.database.repository.PaginatedDomainObjectRepository;
import org.vmalibu.modules.module.exception.PlatformException;

@Repository
public interface ExerciseSourceAccessRepository extends PaginatedDomainObjectRepository<DbExerciseSourceAccess> {

    @Query("from DbExerciseSourceAccess esa where esa.userId = :userId and esa.exerciseSource.id = :sourceId")
    DbExerciseSourceAccess findByUserIdAndSourceId(@Param("userId") String userId, @Param("sourceId") long exerciseSourceId);



    default DbExerciseSourceAccess checkExistenceAndGet(long id) throws PlatformException {
        return PaginatedDomainObjectRepository.super.checkExistenceAndGet(id, DbExerciseSourceAccess.class);
    }
}
