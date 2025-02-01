package org.vmalibu.modules.database.paging;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.vmalibu.modules.database.domainobject.DomainObject;
import org.vmalibu.modules.database.repository.PaginatedDomainObjectRepository;

import java.util.function.Function;

public abstract class AbstractDomainObjectPagination<T extends DomainObject<?>, U, V> implements DomainObjectPagination<T, V> {

    protected final PaginatedDomainObjectRepository<?, T> dao;
    protected final Function<U, V> mapper;

    protected AbstractDomainObjectPagination(@NonNull PaginatedDomainObjectRepository<?, T> dao,
                                             @NonNull Function<U, V> mapper) {
        this.dao = dao;
        this.mapper = mapper;
    }

    public @NonNull PaginatedDto<V> findAll(@NonNull PagingRequest pagingRequest,
                                            @Nullable Specification<T> specification) {
        PageRequest pageRequest = PageRequest.of(pagingRequest.getPage(), pagingRequest.getPageSize(), pagingRequest.getSort());
        Page<U> result = getResult(specification, pageRequest);

        Pageable pageable = result.getPageable();
        return PaginatedDto.<V>builder()
                .result(result.get().map(mapper).toList())
                .page(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalCount(result.getTotalElements())
                .build();
    }

    protected abstract @NonNull Page<U> getResult(@Nullable Specification<T> specification,
                                                  @NonNull PageRequest pageRequest);

}