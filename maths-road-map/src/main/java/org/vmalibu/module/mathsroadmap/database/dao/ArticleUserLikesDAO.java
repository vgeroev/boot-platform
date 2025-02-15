package org.vmalibu.module.mathsroadmap.database.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticleUserLikes;
import org.vmalibu.modules.database.repository.DomainObjectRepository;

import java.util.Optional;

@Repository
public interface ArticleUserLikesDAO extends DomainObjectRepository<DBArticleUserLikes.Id, DBArticleUserLikes> {

    @Query("from DBArticleUserLikes a where a.id.user.id = :fk_user and a.id.article.id = :fk_article")
    Optional<DBArticleUserLikes> findByArticleAndUser(@Param("fk_article") long articleId, @Param("fk_user") long userId);

}
