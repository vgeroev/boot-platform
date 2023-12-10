package org.vmalibu.module.exercises.service.exercise;

import org.vmalibu.modules.utils.OptionalField;

public class ExerciseBuilder {

    private OptionalField<String> problemName = OptionalField.empty();
    private OptionalField<String> problem = OptionalField.empty();
    private OptionalField<ExerciseSolutionStatus> solutionStatus = OptionalField.empty();
    private OptionalField<String> solution = OptionalField.empty();
    private OptionalField<Long> exerciseSourceId = OptionalField.empty();

    //------------------------------------------------------------------------------------------------------------------

    public ExerciseBuilder problemName(String problemName) {
        this.problemName = OptionalField.of(problemName);
        return this;
    }

    public ExerciseBuilder problem(String problem) {
        this.problem = OptionalField.of(problem);
        return this;
    }

    public ExerciseBuilder solutionStatus(ExerciseSolutionStatus solutionStatus) {
        this.solutionStatus = OptionalField.of(solutionStatus);
        return this;
    }

    public ExerciseBuilder solution(String solution) {
        this.solution = OptionalField.of(solution);
        return this;
    }

    public ExerciseBuilder exerciseSourceId(long exerciseSourceId) {
        this.exerciseSourceId = OptionalField.of(exerciseSourceId);
        return this;
    }

    //------------------------------------------------------------------------------------------------------------------

    public boolean isContainProblemName() {
        return problemName.isPresent();
    }

    public boolean isContainProblem() {
        return problem.isPresent();
    }

    public boolean isContainSolutionStatus() {
        return solutionStatus.isPresent();
    }

    public boolean isContainSolution() {
        return solution.isPresent();
    }

    public boolean isContainExerciseSourceId() {
        return exerciseSourceId.isPresent();
    }

    //------------------------------------------------------------------------------------------------------------------

    public String getProblemName() {
        return problemName.get();
    }

    public String getProblem() {
        return problem.get();
    }

    public ExerciseSolutionStatus getSolutionStatus() {
        return solutionStatus.get();
    }

    public String getSolution() {
        return solution.get();
    }

    public Long getExerciseSourceId() {
        return exerciseSourceId.get();
    }
}
