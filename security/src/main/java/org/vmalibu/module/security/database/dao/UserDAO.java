package org.vmalibu.module.security.database.dao;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vmalibu.module.security.database.domainobject.DBAccessRole;
import org.vmalibu.module.security.database.domainobject.DBUser;
import org.vmalibu.modules.database.repository.PaginatedDomainObjectRepository;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.Optional;

@Repository
public interface UserDAO extends PaginatedDomainObjectRepository<DBUser> {

    @Query("from DBUser where username = :username")
    Optional<DBUser> findByUsername(@Param("username") String username);

    @EntityGraph(attributePaths = { DBUser.Fields.accessRoles + "." + DBAccessRole.Fields.privileges })
    @Query("from DBUser where username = :username")
    Optional<DBUser> findWithPrivileges(@Param("username") String username);

    @Query("select count(u.id) > 0 from DBUser u where u.username = :username")
    boolean isExistByUsername(@Param("username") String username);

    @NonNull
    default DBUser checkExistenceAndGet(long id) throws PlatformException {
        return PaginatedDomainObjectRepository.super.checkExistenceAndGet(id, DBUser.class);
    }

}
