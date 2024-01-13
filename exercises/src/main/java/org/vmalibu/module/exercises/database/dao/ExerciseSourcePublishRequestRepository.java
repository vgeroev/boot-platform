package org.vmalibu.module.exercises.database.dao;

import org.springframework.stereotype.Repository;
import org.vmalibu.module.exercises.database.domainobject.DbExerciseSourcePublishRequest;
import org.vmalibu.modules.database.repository.PaginatedDomainObjectRepository;

@Repository
public interface ExerciseSourcePublishRequestRepository
        extends PaginatedDomainObjectRepository<DbExerciseSourcePublishRequest> {

    DbExerciseSourcePublishRequest findByExerciseSourceId(long exerciseSourceId);
}
