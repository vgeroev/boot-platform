package org.vmalibu.module.exercises.service.exercisesourcepublishrequest;

import org.vmalibu.modules.utils.OptionalField;

public class ExerciseSourcePublishRequestCreationBuilder {
    
    private OptionalField<String> userId = OptionalField.empty();
    private OptionalField<Long> exerciseSourceId = OptionalField.empty();

    public ExerciseSourcePublishRequestCreationBuilder withUserId(String userId) {
        this.userId = OptionalField.of(userId);
        return this;
    }

    public ExerciseSourcePublishRequestCreationBuilder withExerciseSourceId(long exerciseSourceId) {
        this.exerciseSourceId = OptionalField.of(exerciseSourceId);
        return this;
    }

    //------------------------------------------------------------------------------------------------------------------

    public boolean isContainUserId() {
        return userId.isPresent();
    }

    public boolean isContainExerciseSourceId() {
        return exerciseSourceId.isPresent();
    }

    //------------------------------------------------------------------------------------------------------------------

    public String getUserId() {
        return userId.get();
    }

    public Long getExerciseSourceId() {
        return exerciseSourceId.get();
    }

}
