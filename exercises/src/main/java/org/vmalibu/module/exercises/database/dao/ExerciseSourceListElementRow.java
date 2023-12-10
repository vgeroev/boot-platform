package org.vmalibu.module.exercises.database.dao;

public interface ExerciseSourceListElementRow {

    long getId();
    String getName();
    String getOwnerId();
    Integer getSolutionStatus();
    int getSolutionCount();
    int getAccessOps();

}
