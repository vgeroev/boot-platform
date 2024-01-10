package org.vmalibu.modules.database.paging;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.data.jpa.domain.Specification;
import org.vmalibu.modules.database.domainobject.DomainObject;

public interface DomainObjectPagination<T extends DomainObject, V> {

    @NonNull PaginatedDto<V> findAll(@NonNull PagingRequest pagingRequest,
                                     @Nullable Specification<T> specification);
}
