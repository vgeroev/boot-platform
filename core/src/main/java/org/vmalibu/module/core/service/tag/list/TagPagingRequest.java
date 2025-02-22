package org.vmalibu.module.core.service.tag.list;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.modules.database.paging.PagingRequest;

public class TagPagingRequest extends PagingRequest {

    private final String searchText;

    public TagPagingRequest(@NonNull Builder builder) {
        super(builder);
        this.searchText = builder.searchText;
    }

    public @Nullable String getSearchText() {
        return searchText;
    }

    public static class Builder extends PagingRequest.AbstractBuilder<Builder> {

        private String searchText;

        public Builder(int page, int pageSize) {
            super(page, pageSize);
        }

        public Builder withSearchText(@Nullable String searchText) {
            this.searchText = searchText;
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
