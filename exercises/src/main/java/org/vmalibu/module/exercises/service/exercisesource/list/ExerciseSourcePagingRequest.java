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

    private final String userId;
    private final OptionalField<Set<AccessOp>> accessOpsFilter;
    private final OptionalField<String> nameFilter;

    protected ExerciseSourcePagingRequest(@NonNull Builder builder) {
        super(builder);
        this.userId = Objects.requireNonNull(builder.userId);
        this.accessOpsFilter = Objects.requireNonNull(builder.accessOpsFilter);
        this.nameFilter = Objects.requireNonNull(builder.nameFilter);
    }

    public static class Builder extends PagingRequest.Builder<Builder> {

        private String userId;
        private OptionalField<Set<AccessOp>> accessOpsFilter = OptionalField.empty();
        private OptionalField<String> nameFilter = OptionalField.empty();

        public Builder(int page, int pageSize) {
            super(page, pageSize);
        }

        public Builder withUserId(@NonNull String userId) {
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
