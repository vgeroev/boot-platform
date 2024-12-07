package org.vmalibu.module.mathsroadmap.database.dao;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticle;
import org.vmalibu.modules.database.repository.PaginatedDomainObjectRepository;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.Optional;

@Repository
public interface ArticleDAO extends PaginatedDomainObjectRepository<DBArticle> {

    @Query("from DBArticle a join fetch a.creator where a.id = :id")
    Optional<DBArticle> findArticleWithUser(@Param("id") long id);

    @NonNull
    default DBArticle checkExistenceAndGet(long id) throws PlatformException {
        return PaginatedDomainObjectRepository.super.checkExistenceAndGet(id, DBArticle.class);
    }
}
