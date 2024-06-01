package org.vmalibu.module.mathsroadmap.service.roadmap.list;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.modules.database.paging.PagingRequest;

public class RoadMapPagingRequest extends PagingRequest {

    private final String titlePrefix;
    private final String creatorUsernamePrefix;

    public RoadMapPagingRequest(@NonNull Builder builder) {
        super(builder);
        this.titlePrefix = builder.titlePrefix;
        this.creatorUsernamePrefix = builder.creatorUsernamePrefix;
    }

    public @Nullable String getTitlePrefix() {
        return titlePrefix;
    }

    public @Nullable String getCreatorUsernamePrefix() {
        return creatorUsernamePrefix;
    }

    public static class Builder extends PagingRequest.AbstractBuilder<Builder> {

        private String titlePrefix;
        private String creatorUsernamePrefix;

        public Builder(int page, int pageSize) {
            super(page, pageSize);
        }

        public Builder withTitlePrefix(@Nullable String titlePrefix) {
            this.titlePrefix = titlePrefix;
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
        public @NonNull RoadMapPagingRequest build() {
            return new RoadMapPagingRequest(this);
        }
    }
}
