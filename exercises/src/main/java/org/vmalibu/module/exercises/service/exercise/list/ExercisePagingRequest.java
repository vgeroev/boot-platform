package org.vmalibu.module.exercises.service.exercise.list;

import lombok.Getter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.module.exercises.service.exercise.ExerciseSolutionStatus;
import org.vmalibu.modules.database.paging.PagingRequest;
import org.vmalibu.modules.utils.OptionalField;

import java.util.Objects;

@Getter
public class ExercisePagingRequest extends PagingRequest {

    private final long exerciseSourceId;
    private final OptionalField<String> problemNameFilter;
    private final OptionalField<ExerciseSolutionStatus> solutionStatusFilter;

    public ExercisePagingRequest(@NonNull Builder builder) {
        super(builder);
        this.exerciseSourceId = builder.exerciseSourceId;
        this.problemNameFilter = Objects.requireNonNull(builder.problemNameFilter);
        this.solutionStatusFilter = Objects.requireNonNull(builder.solutionStatusFilter);
    }

    public static class Builder extends PagingRequest.Builder<Builder> {

        private long exerciseSourceId;
        private OptionalField<String> problemNameFilter = OptionalField.empty();
        private OptionalField<ExerciseSolutionStatus> solutionStatusFilter = OptionalField.empty();

        public Builder(int page, int pageSize) {
            super(page, pageSize);
        }

        public Builder withExerciseSourceId(long exerciseSourceId) {
            this.exerciseSourceId = exerciseSourceId;
            return this;
        }

        public Builder withProblemNameFilter(@NonNull OptionalField<String> problemNameFilter) {
            this.problemNameFilter = problemNameFilter;
            return this;
        }

        public Builder withSolutionStatusFilter(@NonNull OptionalField<ExerciseSolutionStatus> solutionStatusFilter) {
            this.solutionStatusFilter = solutionStatusFilter;
            return this;
        }

        @Override
        protected @NonNull Builder self() {
            return this;
        }

        @Override
        public @NonNull ExercisePagingRequest build() {
            return new ExercisePagingRequest(this);
        }

    }
}
