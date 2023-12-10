package org.vmalibu.module.exercises.service.exercise;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.vmalibu.module.exercises.database.dao.ExerciseRepository;
import org.vmalibu.module.exercises.database.dao.ExerciseSourceRepository;
import org.vmalibu.module.exercises.database.domainobject.DbExercise;
import org.vmalibu.module.exercises.database.domainobject.DbExerciseSource;
import org.vmalibu.module.exercises.service.exercisesource.ExerciseSourceDto;
import org.vmalibu.modules.database.domainobject.DomainObject;
import org.vmalibu.modules.database.paging.DefaultDomainObjectPagination;
import org.vmalibu.modules.database.paging.DomainObjectPagination;
import org.vmalibu.modules.database.paging.PaginatedDto;
import org.vmalibu.modules.database.paging.SortDirection;
import org.vmalibu.modules.module.exception.GeneralExceptionBuilder;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.utils.OptionalField;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExerciseServiceImpl implements ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseSourceRepository exerciseSourceRepository;
    private final DomainObjectPagination<DbExercise, ExerciseListElement> domainObjectPagination;

    @Autowired
    public ExerciseServiceImpl(ExerciseRepository exerciseRepository,
                               ExerciseSourceRepository exerciseSourceRepository) {
        this.exerciseRepository = exerciseRepository;
        this.exerciseSourceRepository = exerciseSourceRepository;
        this.domainObjectPagination = new DefaultDomainObjectPagination<>(exerciseRepository, ExerciseListElement::from);
    }

    @Override
    public @Nullable ExerciseDto get(long id) {
        return exerciseRepository.findById(id).map(ExerciseDto::from).orElse(null);
    }

    @Override
    public @NonNull ExerciseDto create(@NonNull ExerciseBuilder builder) throws PlatformException {
        if (!builder.isContainProblemName() || !StringUtils.hasText(builder.getProblemName())) {
            throw GeneralExceptionBuilder.buildEmptyValueException(DbExercise.Fields.problemName);
        }

        if (!builder.isContainProblem() || !StringUtils.hasText(builder.getProblem())) {
            throw GeneralExceptionBuilder.buildEmptyValueException(DbExercise.Fields.problem);
        }

        if (!builder.isContainSolutionStatus() || builder.getSolutionStatus() == null) {
            throw GeneralExceptionBuilder.buildEmptyValueException(DbExercise.Fields.solutionStatus);
        }

        if (!builder.isContainSolution()) {
            builder.solution(null);
        }

        if (!builder.isContainExerciseSourceId() || builder.getExerciseSourceId() == null) {
            throw GeneralExceptionBuilder.buildEmptyValueException(DbExercise.Fields.exerciseSource);
        }

        DbExercise exercise = new DbExercise();
        setFieldsFor(exercise, builder);
        exerciseRepository.save(exercise);

        return ExerciseDto.from(exercise);
    }

    @Override
    public @NonNull ExerciseDto update(long id, @NonNull ExerciseBuilder builder) throws PlatformException {
        if (builder.isContainProblemName() && !StringUtils.hasText(builder.getProblemName())) {
            throw GeneralExceptionBuilder.buildEmptyValueException(DbExercise.Fields.problemName);
        }

        if (builder.isContainProblem() && !StringUtils.hasText(builder.getProblem())) {
            throw GeneralExceptionBuilder.buildEmptyValueException(DbExercise.Fields.problem);
        }

        if (builder.isContainSolutionStatus() && builder.getSolutionStatus() == null) {
            throw GeneralExceptionBuilder.buildEmptyValueException(DbExercise.Fields.solutionStatus);
        }

        if (builder.isContainExerciseSourceId() && builder.getExerciseSourceId() == null) {
            throw GeneralExceptionBuilder.buildEmptyValueException(DbExercise.Fields.exerciseSource);
        }

        DbExercise exercise = exerciseRepository.checkExistenceAndGet(id);
        setFieldsFor(exercise, builder);
        exerciseRepository.save(exercise);

        return ExerciseDto.from(exercise);
    }

    @Override
    public void delete(long id) {
        exerciseRepository.deleteById(id);
    }

    @Override
    public @Nullable ExerciseSourceDto getExerciseSource(long exerciseId) {
        return exerciseRepository.findWithExerciseSource(exerciseId)
                .map(e -> ExerciseSourceDto.from(e.getExerciseSource()))
                .orElse(null);
    }

    @Override
    public @NonNull PaginatedDto<ExerciseListElement> findAll(int page,
                                                              int pageSize,
                                                              @Nullable ExerciseSortField sortField,
                                                              @Nullable SortDirection direction,
                                                              @NonNull OptionalField<Long> exerciseSourceIdFilter,
                                                              @NonNull OptionalField<String> problemNameFilter,
                                                              @NonNull OptionalField<ExerciseSolutionStatus> solutionStatusFilter) {
        return domainObjectPagination.findAll(page, pageSize, direction, sortField,
                buildSpecification(exerciseSourceIdFilter, problemNameFilter, solutionStatusFilter));
    }

    @Override
    public @NonNull PaginatedDto<ExerciseListElement> findAll(@Nullable Integer limit,
                                                              @Nullable ExerciseSortField sortField,
                                                              @Nullable SortDirection direction,
                                                              @NonNull OptionalField<Long> exerciseSourceIdFilter,
                                                              @NonNull OptionalField<String> problemNameFilter,
                                                              @NonNull OptionalField<ExerciseSolutionStatus> solutionStatusFilter) {
        return domainObjectPagination.findAll(limit, direction, sortField,
                buildSpecification(exerciseSourceIdFilter, problemNameFilter, solutionStatusFilter));
    }

    private void setFieldsFor(
            DbExercise exercise,
            ExerciseBuilder builder
    ) throws PlatformException {
        String problemName;
        if (builder.isContainProblemName()) {
            problemName = builder.getProblemName();
        } else {
            problemName = exercise.getProblemName();
        }

        String problem;
        if (builder.isContainProblem()) {
            problem = builder.getProblem();
        } else {
            problem = exercise.getProblem();
        }

        ExerciseSolutionStatus solutionStatus;
        if (builder.isContainSolutionStatus()) {
            solutionStatus = builder.getSolutionStatus();
        } else {
            solutionStatus = exercise.getSolutionStatus();
        }

        String solution;
        if (builder.isContainSolution()) {
            solution = builder.getSolution();
        } else {
            solution = exercise.getSolution();
        }

        DbExerciseSource exerciseSource;
        if (builder.isContainExerciseSourceId()) {
            exerciseSource = exerciseSourceRepository.checkExistenceAndGet(builder.getExerciseSourceId());
        } else {
            exerciseSource = exercise.getExerciseSource();
        }

        if (builder.isContainProblemName() || builder.isContainExerciseSourceId()) {
            exerciseRepository.checkUniqueness(
                    exercise,
                    () -> exerciseRepository.findByProblemNameAndExerciseSourceId(problemName, exerciseSource.getId()),
                    () -> GeneralExceptionBuilder.buildNotUniqueDomainObjectException(
                            DbExercise.class, DbExercise.Fields.problemName, problemName
                    )
            );
        }

        exercise.setProblemName(problemName);
        exercise.setProblem(problem);
        exercise.setSolutionStatus(solutionStatus);
        exercise.setSolution(solution);
        exercise.setExerciseSource(exerciseSource);
    }

    private static Specification<DbExercise> buildSpecification(
            OptionalField<Long> exerciseSourceIdFilter,
            OptionalField<String> problemNameFilter,
            OptionalField<ExerciseSolutionStatus> solutionStatusFilter
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (exerciseSourceIdFilter.isPresent()) {
                predicates.add(getExerciseSourceIdFilterPredicate(root, cb, exerciseSourceIdFilter.get()));
            }

            if (problemNameFilter.isPresent()) {
                predicates.add(getProblemNameFilterPredicate(root, cb, problemNameFilter.get()));
            }

            if (solutionStatusFilter.isPresent()) {
                predicates.add(getSolutionStatusFilterPredicate(root, cb, solutionStatusFilter.get()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate getExerciseSourceIdFilterPredicate(
            Root<DbExercise> root,
            CriteriaBuilder cb,
            Long exerciseSourceId
    ) {
        Path<Long> idPath = root.get(DbExercise.Fields.exerciseSource).get(DomainObject.Fields.id);
        return exerciseSourceId != null ? cb.equal(idPath, exerciseSourceId) : cb.isNull(idPath);
    }

    private static Predicate getProblemNameFilterPredicate(
            Root<DbExercise> root,
            CriteriaBuilder cb,
            String problemName
    ) {
        Path<String> problemNamePath = root.get(DbExercise.Fields.problemName);
        return problemName != null
                ? cb.like(cb.upper(problemNamePath), "%" + problemName.toUpperCase() + "%")
                : cb.isNull(problemNamePath);
    }

    private static Predicate getSolutionStatusFilterPredicate(
            Root<DbExercise> root,
            CriteriaBuilder cb,
            ExerciseSolutionStatus solutionStatus
    ) {
        Path<ExerciseSolutionStatus> solutionStatusPath = root.get(DbExercise.Fields.solutionStatus);
        return solutionStatus != null
                ? cb.equal(solutionStatusPath, solutionStatus)
                : cb.isNull(solutionStatusPath);
    }

}
