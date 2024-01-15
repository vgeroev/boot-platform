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

    protected PagingRequest(@NonNull AbstractBuilder<?> builder) {
        this.page = builder.page;
        this.pageSize = builder.pageSize;
        this.sort = Objects.requireNonNull(builder.sort);
    }

    public abstract static class AbstractBuilder<T extends AbstractBuilder<T>> {

        private final int page;
        private final int pageSize;
        private Sort sort;

        protected AbstractBuilder(int page, int pageSize) {
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

        protected abstract @NonNull T self();

        public abstract @NonNull PagingRequest build();
    }

    public static class Builder extends AbstractBuilder<Builder> {

        public Builder(int page, int pageSize) {
            super(page, pageSize);
        }

        @Override
        protected @NonNull Builder self() {
            return this;
        }

        public @NonNull PagingRequest build() {
            return new PagingRequest(this);
        }
    }
}
