package org.vmalibu.module.mathsroadmap.database.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vmalibu.module.core.database.domainobject.DBTag;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticleTag;
import org.vmalibu.modules.database.repository.DomainObjectRepository;

import java.util.List;
import java.util.Set;

@Repository
public interface ArticleTagDAO extends DomainObjectRepository<DBArticleTag.Id, DBArticleTag> {

    @Query("select count(*) > 0 from DBArticleTag a where a.id.article.id = :articleId and a.id.tag.id = :tagId")
    boolean existsById(@Param("articleId") long articleId, @Param("tagId") long tagId);

    @Query("select t from DBTag t inner join DBArticleTag at on t.id = at.id.tag.id where at.id.article.id = :id")
    List<DBTag> findTagsByArticle(@Param("id") long articleId);

    @Modifying
    @Query(value = "DELETE FROM "
            + DBArticleTag.DB_TABLE_NAME
            + " a WHERE"
            + " a." + DBArticleTag.DB_FK_TAG
            + " in :tagIds RETURNING " + DBArticleTag.DB_FK_ARTICLE, nativeQuery = true)
    Set<Long> deleteAllByTags(@Param("tagIds") Set<Long> ids);
}
