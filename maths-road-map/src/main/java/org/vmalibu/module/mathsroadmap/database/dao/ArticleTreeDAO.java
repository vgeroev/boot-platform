package org.vmalibu.module.mathsroadmap.database.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticleTree;

import java.util.Set;

@Repository
public interface ArticleTreeDAO extends JpaRepository<DBArticleTree, DBArticleTree> {

    @Query("select a from DBArticleTree a where a.id.fkPrev in :ids")
    Set<DBArticleTree> getNextArticles(@Param("ids") Set<Long> ids);
}
