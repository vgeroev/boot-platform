package org.vmalibu.module.mathsroadmap.service.article.list;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.module.mathsroadmap.database.dao.ArticleDAO;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticle;
import org.vmalibu.modules.database.paging.DomainObjectPaginationImpl;

public class ArticlePagination extends DomainObjectPaginationImpl<DBArticle, ArticleListElement> {

    public ArticlePagination(@NonNull ArticleDAO dao) {
        super(dao, ArticleListElement::from);
    }

}
