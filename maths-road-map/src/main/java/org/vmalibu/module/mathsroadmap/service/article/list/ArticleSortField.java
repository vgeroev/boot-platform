package org.vmalibu.module.mathsroadmap.service.article.list;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.data.domain.Sort;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticle;
import org.vmalibu.modules.database.paging.SortField;

public enum ArticleSortField implements SortField {

    CREATED_AT(Sort.by(DBArticle.DB_CREATED_AT));

    private final Sort sort;
    ArticleSortField(Sort sort) {
        this.sort = sort;
    }

    @Override
    public @NonNull Sort getSort() {
        return sort;
    }
}
