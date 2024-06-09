package org.vmalibu.module.mathsroadmap.service.roadmap.list;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.data.domain.Sort;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBRoadMap;
import org.vmalibu.modules.database.paging.SortField;

public enum RoadMapSortField implements SortField {

    CREATED_AT(Sort.by(DBRoadMap.Fields.createdAt));

    private final Sort sort;
    RoadMapSortField(Sort sort) {
        this.sort = sort;
    }

    @Override
    public @NonNull Sort getSort() {
        return sort;
    }

}
