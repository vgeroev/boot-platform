package org.vmalibu.module.exercises.service.exercisesource.list;

import lombok.Getter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.module.security.access.AccessOp;
import org.vmalibu.modules.database.paging.PagingRequest;
import org.vmalibu.modules.utils.OptionalField;

import java.util.Objects;
import java.util.Set;

@Getter
public class ExerciseSourcePagingRequest extends PagingRequest {

    private final OptionalField<String> userId;
    private final OptionalField<Set<AccessOp>> accessOpsFilter;
    private final OptionalField<String> nameFilter;
    private final OptionalField<Boolean> publishedFilter;

    protected ExerciseSourcePagingRequest(@NonNull Builder builder) {
        super(builder);
        this.userId = Objects.requireNonNull(builder.userId);
        this.accessOpsFilter = Objects.requireNonNull(builder.accessOpsFilter);
        this.nameFilter = Objects.requireNonNull(builder.nameFilter);
        this.publishedFilter = Objects.requireNonNull(builder.publishedFilter);
    }

    public static class Builder extends AbstractBuilder<Builder> {

        private OptionalField<String> userId = OptionalField.empty();
        private OptionalField<Set<AccessOp>> accessOpsFilter = OptionalField.empty();
        private OptionalField<String> nameFilter = OptionalField.empty();
        private OptionalField<Boolean> publishedFilter = OptionalField.empty();

        public Builder(int page, int pageSize) {
            super(page, pageSize);
        }

        public Builder withUserId(@NonNull OptionalField<String> userId) {
            this.userId = userId;
            return this;
        }

        public Builder withAccessOpsFilter(@NonNull OptionalField<Set<AccessOp>> accessOpsFilter) {
            this.accessOpsFilter = accessOpsFilter;
            return this;
        }

        public Builder withNameFilter(@NonNull OptionalField<String> nameFilter) {
            this.nameFilter = nameFilter;
            return this;
        }

        public Builder withPublishedFilter(@NonNull OptionalField<Boolean> publishedFilter) {
            this.publishedFilter = publishedFilter;
            return this;
        }

        @Override
        protected @NonNull Builder self() {
            return this;
        }

        @Override
        public @NonNull ExerciseSourcePagingRequest build() {
            return new ExerciseSourcePagingRequest(this);
        }
    }
}
