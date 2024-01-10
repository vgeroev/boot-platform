package org.vmalibu.modules.database.paging;

import lombok.Getter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.data.domain.Sort;

import java.util.Objects;
import java.util.Optional;

@Getter
public class PagingRequest {

    private final int page;
    private final int pageSize;
    private final Sort sort;

    protected PagingRequest(@NonNull Builder<?> builder) {
        this.page = builder.page;
        this.pageSize = builder.pageSize;
        this.sort = Objects.requireNonNull(builder.sort);
    }

    public static class Builder<T extends Builder<T>> {

        private final int page;
        private final int pageSize;
        private Sort sort;

        public Builder(int page, int pageSize) {
            this.page = page;
            this.pageSize = pageSize;
        }

        public T withSort(@NonNull Sort sort) {
            this.sort = sort;
            return self();
        }

        public T withSort(@Nullable SortField sortField, @Nullable SortDirection sortDirection) {
            Sort newSort = Optional.ofNullable(sortField)
                    .map(SortField::getSort)
                    .orElse(Sort.unsorted());

            if (sortDirection != null) {
                newSort = switch (sortDirection) {
                    case ASC -> newSort.ascending();
                    case DESC -> newSort.descending();
                };
            }

            return withSort(newSort);
        }

        @SuppressWarnings("unchecked")
        protected @NonNull T self() {
            return (T) this;
        }

        public @NonNull PagingRequest build() {
            return new PagingRequest(this);
        }
    }
}
