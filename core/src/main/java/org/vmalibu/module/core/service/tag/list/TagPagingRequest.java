package org.vmalibu.module.core.service.tag.list;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.modules.database.paging.PagingRequest;

import java.util.Set;

public class TagPagingRequest extends PagingRequest {

    private final String searchText;
    private final Set<Long> filterIds;

    public TagPagingRequest(@NonNull Builder builder) {
        super(builder);
        this.searchText = builder.searchText;
        this.filterIds = builder.filterIds;
    }

    public @Nullable String getSearchText() {
        return searchText;
    }

    public @Nullable Set<Long> getFilterIds() {
        return filterIds;
    }

    public static class Builder extends PagingRequest.AbstractBuilder<Builder> {

        private String searchText;
        private Set<Long> filterIds;

        public Builder(int page, int pageSize) {
            super(page, pageSize);
        }

        public Builder withSearchText(@Nullable String searchText) {
            this.searchText = searchText;
            return self();
        }

        public Builder withFilterIds(@Nullable Set<Long> filterIds) {
            this.filterIds = filterIds;
            return self();
        }

        @Override
        protected @NonNull Builder self() {
            return this;
        }

        @Override
        public @NonNull TagPagingRequest build() {
            return new TagPagingRequest(this);
        }
    }
}
