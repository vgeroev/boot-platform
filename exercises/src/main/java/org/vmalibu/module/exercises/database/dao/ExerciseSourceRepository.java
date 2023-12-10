package org.vmalibu.module.exercises.database.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vmalibu.module.exercises.database.domainobject.DbExerciseSource;
import org.vmalibu.modules.database.repository.PaginatedDomainObjectRepository;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.List;

@Repository
public interface ExerciseSourceRepository extends PaginatedDomainObjectRepository<DbExerciseSource> {

    DbExerciseSource findByNameAndOwnerId(String name, String ownerId);

//    @Query("""
//        select
//            sq1.id as id,
//            sq1.name as name,
//            sq1.ownerId as ownerId,
//            sq1.solutionStatus as solutionStatus,
//            sq1.solutionCount as solutionCount,
//            esa.accessOps as accessOps
//        from
//            DbExerciseSourceAccess as esa
//        inner join (
//                select
//                    id as id,
//                    name as name,
//                    ownerId as ownerId,
//                    sq2.solutionStatus as solutionStatus,
//                    sq2.solutionCount as solutionCount
//                from
//                    DbExerciseSource
//                inner join (
//                    select
//                        solutionStatus as solutionStatus,
//                        count(*) as solutionCount,
//                        exerciseSource as exerciseSource
//                    from
//                        DbExercise
//                    group by
//                        solutionStatus, exerciseSource
//                ) as sq2 on id = sq2.exerciseSource.id
//        ) as sq1 on esa.exerciseSource.id = sq1.id
//        where esa.userId = :userId
//        """)
    @Query(value = """
            select
                sq2.id as id,
                sq2.name as name,
                sq2.owner_id as ownerId,
                sq2.access_ops as accessOps,
                e.solution_status as solutionStatus,
                count(*) as solutionCount
            from
                exercises_exercise as e
            right join (
                select
                    id,
                    name,
                    owner_id,
                    sq1.access_ops as access_ops
                from
                    exercises_exercise_source as es
                join (
                    select
                        user_id,
                        access_ops,
                        exercise_source_id
                    from
                        exercises_exercise_source_access as esa
                    where user_id = :userId and access_ops & :accessOps = :accessOps
                ) as sq1 on es.id = sq1.exercise_source_id
            ) as sq2 on e.exercise_source_id = sq2.id
            group by e.solution_status, sq2.id, sq2.name, sq2.owner_id, sq2.access_ops;
    """, nativeQuery = true)
    List<ExerciseSourceListElementRow> listWithStats(@Param("userId") String userId,
                                                     @Param("accessOps") int accessOps);


    default DbExerciseSource checkExistenceAndGet(long id) throws PlatformException {
        return PaginatedDomainObjectRepository.super.checkExistenceAndGet(id, DbExerciseSource.class);
    }
}
