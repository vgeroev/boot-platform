package org.vmalibu.modules.database.paging;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.modules.database.domainobject.DomainObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DomainObjectPagination<Domain extends DomainObject, Dto> {

    private final BiFunction<Specification<Domain>, PageRequest, Page<Domain>> elementsGetter;
    private final Function<Domain, Dto> mapper;

    public DomainObjectPagination(@NonNull BiFunction<Specification<Domain>, PageRequest, Page<Domain>> elementsGetter,
                                  @NonNull Function<Domain, Dto> mapper) {
        this.elementsGetter = elementsGetter;
        this.mapper = mapper;
    }

    public @NonNull PaginatedDto<Dto> findAll(int page,
                                              int pageSize,
                                              @Nullable SortDirection direction,
                                              @Nullable SortField sortField,
                                              @Nullable Specification<Domain> specification) {
        PageRequest pageRequest = PageRequest.of(page, pageSize, resolveSort(sortField, direction));
        Page<Domain> result = elementsGetter.apply(specification, pageRequest);

        return PaginatedDto.<Dto>builder()
                .result(result.get().map(mapper).toList())
                .page(result.getPageable().getPageNumber())
                .pageSize(result.getPageable().getPageSize())
                .totalCount(result.getTotalElements())
                .build();
    }

    public @NonNull PaginatedDto<Dto> findAll(@Nullable Integer limit,
                                              @Nullable SortDirection direction,
                                              @Nullable SortField sortField,
                                              @Nullable Specification<Domain> specification) {
        return findAll(0, Objects.requireNonNullElse(limit, Integer.MAX_VALUE), direction, sortField, specification);
    }

    private static Sort resolveSort(SortField sortField, SortDirection direction) {
        Sort sort = sortField != null ? Sort.by(sortField.getFieldName()) : Sort.unsorted();
        return direction == SortDirection.DESC ? sort.descending() : sort.ascending();
    }
}
