package org.vmalibu.module.exercises.service.exercisesource;

import jakarta.persistence.criteria.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.vmalibu.module.exercises.database.dao.ExerciseSourceAccessRepository;
import org.vmalibu.module.exercises.database.dao.ExerciseSourceListElementRow;
import org.vmalibu.module.exercises.database.dao.ExerciseSourceRepository;
import org.vmalibu.module.exercises.database.domainobject.DbExercise;
import org.vmalibu.module.exercises.database.domainobject.DbExerciseSource;
import org.vmalibu.module.exercises.database.domainobject.DbExerciseSourceAccess;
import org.vmalibu.module.exercises.service.exercise.ExerciseSolutionStatus;
import org.vmalibu.module.security.access.AccessOp;
import org.vmalibu.module.security.database.converter.AccessOpsConverter;
import org.vmalibu.modules.database.paging.DefaultDomainObjectPagination;
import org.vmalibu.modules.database.paging.DomainObjectPagination;
import org.vmalibu.modules.database.paging.PaginatedDto;
import org.vmalibu.modules.database.paging.SortDirection;
import org.vmalibu.modules.module.exception.GeneralExceptionBuilder;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.utils.OptionalField;
import org.vmalibu.modules.utils.database.DatabaseFunctionNames;

import java.util.*;

@Service
public class ExerciseSourceServiceImpl implements ExerciseSourceService {

    private final ExerciseSourceRepository exerciseSourceRepository;
    private final ExerciseSourceAccessRepository exerciseSourceAccessRepository;
    private final DomainObjectPagination<DbExerciseSourceAccess, ExerciseSourceListElement> domainObjectPagination;

    @Autowired
    public ExerciseSourceServiceImpl(@NonNull ExerciseSourceRepository exerciseSourceRepository,
                                     @NonNull ExerciseSourceAccessRepository exerciseSourceAccessRepository) {
        this.exerciseSourceRepository = exerciseSourceRepository;
        this.exerciseSourceAccessRepository = exerciseSourceAccessRepository;
        this.domainObjectPagination = new DefaultDomainObjectPagination<>(
                exerciseSourceAccessRepository, ExerciseSourceListElement::from);
    }

    @Override
    public @Nullable ExerciseSourceDto get(long id) {
        return exerciseSourceRepository.findById(id).map(ExerciseSourceDto::from).orElse(null);
    }

    @Override
    public @NonNull ExerciseSourceDto create(@NonNull ExerciseSourceBuilder builder) throws PlatformException {
        if (!builder.isContainName() || !StringUtils.hasText(builder.getName())) {
            throw GeneralExceptionBuilder.buildEmptyValueException(DbExerciseSource.class, DbExerciseSource.FIELD_NAME);
        }

        if (!builder.isContainOwnerId() || !StringUtils.hasText(builder.getOwnerId())) {
            throw GeneralExceptionBuilder.buildEmptyValueException(DbExerciseSource.class, DbExerciseSource.FIELD_OWNER_ID);
        }

        DbExerciseSource exerciseSource = new DbExerciseSource();
        setFields(exerciseSource, builder);
        exerciseSourceRepository.save(exerciseSource);
        assignFullAccess(exerciseSource);
        return ExerciseSourceDto.from(exerciseSource);
    }

    @Override
    public @NonNull ExerciseSourceDto update(long id, @NonNull ExerciseSourceBuilder builder) throws PlatformException {
        if (builder.isContainName() && !StringUtils.hasText(builder.getName())) {
            throw GeneralExceptionBuilder.buildEmptyValueException(DbExerciseSource.class, DbExerciseSource.FIELD_NAME);
        }

        if (builder.isContainOwnerId() && !StringUtils.hasText(builder.getOwnerId())) {
            throw GeneralExceptionBuilder.buildEmptyValueException(DbExerciseSource.class, DbExerciseSource.FIELD_OWNER_ID);
        }

        DbExerciseSource exerciseSource = exerciseSourceRepository.checkExistenceAndGet(id);

        setFields(exerciseSource, builder);
        exerciseSourceRepository.save(exerciseSource);
        return ExerciseSourceDto.from(exerciseSource);
    }

    @Override
    public void delete(long id) {
        exerciseSourceRepository.deleteById(id);
    }

    @Override
    public @NonNull PaginatedDto<ExerciseSourceListElement> findAll(int page,
                                                                    int pageSize,
                                                                    @Nullable ExerciseSourceSortField sortField,
                                                                    @Nullable SortDirection direction,
                                                                    @NonNull OptionalField<String> userIdFilter,
                                                                    @NonNull OptionalField<Set<AccessOp>> accessOpsFilter,
                                                                    @NonNull OptionalField<String> nameFilter) {
        return domainObjectPagination.findAll(
                page, pageSize, direction, sortField, buildSpecification(userIdFilter, accessOpsFilter, nameFilter));
    }

    @Override
    public @NonNull PaginatedDto<ExerciseSourceListElement> findAll(@Nullable Integer limit,
                                                                    @Nullable ExerciseSourceSortField sortField,
                                                                    @Nullable SortDirection direction,
                                                                    @NonNull OptionalField<String> userIdFilter,
                                                                    @NonNull OptionalField<Set<AccessOp>> accessOpsFilter,
                                                                    @NonNull OptionalField<String> nameFilter) {
        return domainObjectPagination.findAll(
                limit, direction, sortField, buildSpecification(userIdFilter, accessOpsFilter, nameFilter));
    }

    @Override
    public @NonNull List<ExerciseSourceListElement> findAll(@NonNull String userId,
                                                            @NonNull Set<AccessOp> accessOpsFilter) {
        AccessOpsConverter accessOpsConverter = new AccessOpsConverter();
        Integer accessOps = accessOpsConverter.convertToDatabaseColumn(accessOpsFilter);
        List<ExerciseSourceListElementRow> listElementRows = exerciseSourceRepository.listWithStats(userId, accessOps);

        Map<Long, Map<ExerciseSolutionStatus, Integer>> solutionStatuses = mergeSolutionStatuses(listElementRows);

        Map<Long, ExerciseSourceListElement> listElements = new HashMap<>();
        for (ExerciseSourceListElementRow listElementRow : listElementRows) {
            listElements.computeIfAbsent(
                    listElementRow.getId(),
                    id -> ExerciseSourceListElement.builder()
                            .accessOps(accessOpsConverter.convertToEntityAttribute(listElementRow.getAccessOps()))
                            .exerciseSource(
                                    ExerciseSourceDto.builder()
                                            .id(id)
                                            .name(listElementRow.getName())
                                            .ownerId(listElementRow.getOwnerId())
                                            .build()
                            ).stats(
                                    ExerciseSourceStats.builder()
                                            .solutionStatusesStats(solutionStatuses.get(id))
                                            .build()
                            ).build()
            );
        }

        return new ArrayList<>(listElements.values());
    }

    private static Map<Long, Map<ExerciseSolutionStatus, Integer>> mergeSolutionStatuses(List<ExerciseSourceListElementRow> listElementRows) {
        Map<Long, Map<ExerciseSolutionStatus, Integer>> solutionStatuses = new HashMap<>();
        for (ExerciseSourceListElementRow listElementRow : listElementRows) {
            solutionStatuses.compute(listElementRow.getId(), (k, v) -> {
                ExerciseSolutionStatus solutionStatus = ExerciseSolutionStatus.from(listElementRow.getSolutionStatus());
                if (solutionStatus == null) {
                    return null;
                }

                if (v == null) {
                    v = new EnumMap<>(ExerciseSolutionStatus.class);
                }

                v.put(solutionStatus, listElementRow.getSolutionCount());
                return v;
            });
        }
        return solutionStatuses;
    }

    private void assignFullAccess(DbExerciseSource exerciseSource) {
        DbExerciseSourceAccess exerciseSourceAccess = new DbExerciseSourceAccess();
        exerciseSourceAccess.setExerciseSource(exerciseSource);
        exerciseSourceAccess.setUserId(exerciseSource.getOwnerId());
        exerciseSourceAccess.setAccessOps(EnumSet.allOf(AccessOp.class));
        exerciseSourceAccessRepository.save(exerciseSourceAccess);
    }

    private void setFields(DbExerciseSource exerciseSource,
                           ExerciseSourceBuilder builder) throws PlatformException {
        String ownerId;
        if (builder.isContainOwnerId()) {
            ownerId = builder.getOwnerId();
        } else {
            ownerId = exerciseSource.getOwnerId();
        }

        String name;
        if (builder.isContainName()) {
            name = builder.getName();
            exerciseSourceRepository.checkUniqueness(
                    exerciseSource,
                    () -> exerciseSourceRepository.findByNameAndOwnerId(name, ownerId),
                    () -> GeneralExceptionBuilder.buildNotUniqueDomainObjectException(
                            DbExerciseSource.class, DbExerciseSource.FIELD_NAME, name)
            );
        } else {
            name = exerciseSource.getName();
        }

        exerciseSource.setName(name);
        exerciseSource.setOwnerId(ownerId);
    }

    private static Specification<DbExerciseSourceAccess> buildSpecification(
            OptionalField<String> userIdFilter,
            OptionalField<Set<AccessOp>> accessOpsFilter,
            OptionalField<String> nameFilter
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            root.fetch(DbExercise.Fields.exerciseSource);

            if (userIdFilter.isPresent()) {
                predicates.add(getUserIdFilterPredicate(root, cb, userIdFilter.get()));
            }

            if (accessOpsFilter.isPresent()) {
                predicates.add(getAccessOpsFilterPredicate(root, cb, accessOpsFilter.get()));
            }

            if (nameFilter.isPresent()) {
                predicates.add(getNameFilter(root, cb, nameFilter.get()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate getUserIdFilterPredicate(Root<DbExerciseSourceAccess> root, CriteriaBuilder cb, String userId) {
        Path<Object> userIdPath = root.get(DbExerciseSourceAccess.Fields.userId);
        return userId != null ? cb.equal(userIdPath, userId) : cb.isNull(userIdPath);
    }

    private static Predicate getAccessOpsFilterPredicate(Root<DbExerciseSourceAccess> root,
                                                         CriteriaBuilder cb,
                                                         Set<AccessOp> accessOps) {
        Path<String> accessOpsPath = root.get(DbExerciseSourceAccess.Fields.accessOps);
        if (accessOps == null) {
            return cb.isNull(accessOpsPath);
        } else {
            Integer accessOpsValue = new AccessOpsConverter().convertToDatabaseColumn(accessOps);
            Expression<Integer> accessOpsValueExp = cb.literal(accessOpsValue);
            Expression<Integer> function = cb.function(
                    DatabaseFunctionNames.BIT_AND, Integer.class, accessOpsPath, accessOpsValueExp);
            return cb.equal(function, accessOpsValue);
        }
    }

    private static Predicate getNameFilter(Root<DbExerciseSourceAccess> root,
                                           CriteriaBuilder cb,
                                           String name) {
        Path<String> namePath = root
                .get(DbExerciseSourceAccess.Fields.exerciseSource)
                .get(DbExerciseSource.Fields.name);
        return name != null
                ? cb.like(cb.upper(namePath), "%" + name.toUpperCase() + "%")
                : cb.isNull(namePath);
    }

}
