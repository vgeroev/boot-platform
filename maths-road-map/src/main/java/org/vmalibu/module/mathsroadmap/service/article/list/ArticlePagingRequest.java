package org.vmalibu.module.mathsroadmap.service.article.list;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.modules.database.paging.PagingRequest;

public class ArticlePagingRequest extends PagingRequest {

    private final String searchText;
    private final String creatorUsernamePrefix;

    public ArticlePagingRequest(@NonNull Builder builder) {
        super(builder);
        this.searchText = builder.searchText;
        this.creatorUsernamePrefix = builder.creatorUsernamePrefix;
    }

    public @Nullable String getSearchText() {
        return searchText;
    }

    public @Nullable String getCreatorUsernamePrefix() {
        return creatorUsernamePrefix;
    }

    public static class Builder extends PagingRequest.AbstractBuilder<Builder> {

        private String searchText;
        private String creatorUsernamePrefix;

        public Builder(int page, int pageSize) {
            super(page, pageSize);
        }

        public Builder withSearchText(@Nullable String searchText) {
            this.searchText = searchText;
            return self();
        }

        public Builder withCreatorUsernamePrefix(@Nullable String creatorUsernamePrefix) {
            this.creatorUsernamePrefix = creatorUsernamePrefix;
            return self();
        }

        @Override
        protected @NonNull Builder self() {
            return this;
        }

        @Override
        public @NonNull ArticlePagingRequest build() {
            return new ArticlePagingRequest(this);
        }
    }
}
