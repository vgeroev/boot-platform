package org.vmalibu.module.mathsroadmap.database.dao;

import jakarta.persistence.LockModeType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticle;
import org.vmalibu.modules.database.repository.PaginatedDomainObjectRepository;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.Optional;

@Repository
public interface ArticleDAO extends PaginatedDomainObjectRepository<Long, DBArticle> {

    @EntityGraph(attributePaths = { DBArticle.Fields.creator, DBArticle.Fields.tags })
    @Query("from DBArticle a where a.id = :id")
    Optional<DBArticle> findArticlePage(@Param("id") long id);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("select a.id from DBArticle a where a.id = :id")
    Optional<Long> lockOnPessimisticWrite(@Param("id") long id);

    @Query("update DBArticle a set a.likes = a.likes - 1, a.dislikes = a.dislikes + 1 where a.id = :id")
    @Modifying
    void swapLikeOnDislike(@Param("id") long id);

    @Query("update DBArticle a set a.likes = a.likes + 1, a.dislikes = a.dislikes - 1 where a.id = :id")
    @Modifying
    void swapDislikeOnLike(@Param("id") long id);

    @Query("update DBArticle a set a.likes = a.likes - 1 where a.id = :id")
    @Modifying
    void decrementLikes(@Param("id") long id);

    @Query("update DBArticle a set a.likes = a.likes + 1 where a.id = :id")
    @Modifying
    void incrementLikes(@Param("id") long id);

    @Query("update DBArticle a set a.dislikes = a.dislikes - 1 where a.id = :id")
    @Modifying
    void decrementDislikes(@Param("id") long id);

    @Query("update DBArticle a set a.dislikes = a.dislikes + 1 where a.id = :id")
    @Modifying
    void incrementDislikes(@Param("id") long id);

    @NonNull
    default DBArticle checkExistenceAndGet(long id) throws PlatformException {
        return PaginatedDomainObjectRepository.super.checkExistenceAndGet(id, DBArticle.class);
    }
}
