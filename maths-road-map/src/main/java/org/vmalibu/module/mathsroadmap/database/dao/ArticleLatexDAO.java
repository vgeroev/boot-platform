package org.vmalibu.module.mathsroadmap.database.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticleLatex;
import org.vmalibu.modules.database.repository.DomainObjectRepository;

@Repository
public interface ArticleLatexDAO extends DomainObjectRepository<DBArticleLatex> {

    @Query("update DBArticleLatex al set configuration = :configuration where al.id = :id")
    @Modifying
    void updateConfiguration(@Param("id") long id, @Param("configuration") String configuration);

    @Query("update DBArticleLatex al set latex = :latex where al.id = :id")
    @Modifying
    void updateLatex(@Param("id") long id, @Param("latex") String latex);

    @Query("update DBArticleLatex al set latex = :latex, configuration = :configuration where al.id = :id")
    @Modifying
    void updateLatex(@Param("id") long id, @Param("latex") String latex, @Param("configuration") String configuration);
}
