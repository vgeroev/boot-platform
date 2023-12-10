package org.vmalibu.module.exercises.service.exercisesource;

import org.vmalibu.modules.utils.OptionalField;

public class ExerciseSourceBuilder {

    private OptionalField<String> name = OptionalField.empty();
    private OptionalField<String> ownerId = OptionalField.empty();

    public ExerciseSourceBuilder name(String name) {
        this.name = OptionalField.of(name);
        return this;
    }

    public ExerciseSourceBuilder ownerId(String ownerId) {
        this.ownerId = OptionalField.of(ownerId);
        return this;
    }

    //------------------------------------------------------------------------------------------------------------------

    public boolean isContainName() {
        return name.isPresent();
    }

    public boolean isContainOwnerId() {
        return ownerId.isPresent();
    }

    //------------------------------------------------------------------------------------------------------------------

    public String getName() {
        return name.get();
    }

    public String getOwnerId() {
        return ownerId.get();
    }

}
