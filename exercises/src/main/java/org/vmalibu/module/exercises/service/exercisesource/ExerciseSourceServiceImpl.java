package org.vmalibu.module.exercises.service.exercisesource;

import jakarta.persistence.criteria.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.vmalibu.module.exercises.database.dao.ExerciseSourceAccessRepository;
import org.vmalibu.module.exercises.database.dao.ExerciseSourceRepository;
import org.vmalibu.module.exercises.database.domainobject.DbExerciseSource;
import org.vmalibu.module.exercises.database.domainobject.DbExerciseSourceAccess;
import org.vmalibu.module.exercises.service.exercisesource.list.ExerciseSourceListElement;
import org.vmalibu.module.exercises.service.exercisesource.list.ExerciseSourcePagingRequest;
import org.vmalibu.module.security.access.AccessOp;
import org.vmalibu.module.security.database.converter.AccessOpsConverter;
import org.vmalibu.modules.database.paging.DomainObjectPagination;
import org.vmalibu.modules.database.paging.DomainObjectPaginationImpl;
import org.vmalibu.modules.database.paging.PaginatedDto;
import org.vmalibu.modules.module.exception.GeneralExceptionBuilder;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.utils.OptionalField;
import org.vmalibu.modules.utils.database.DatabaseFunctionNames;

import java.util.*;

@Service
public class ExerciseSourceServiceImpl implements ExerciseSourceService {

    private final ExerciseSourceRepository exerciseSourceRepository;
    private final ExerciseSourceAccessRepository exerciseSourceAccessRepository;
    private final DomainObjectPagination<DbExerciseSource, ExerciseSourceListElement> domainObjectPagination;

    @Autowired
    public ExerciseSourceServiceImpl(@NonNull ExerciseSourceRepository exerciseSourceRepository,
                                     @NonNull ExerciseSourceAccessRepository exerciseSourceAccessRepository) {
        this.exerciseSourceRepository = exerciseSourceRepository;
        this.exerciseSourceAccessRepository = exerciseSourceAccessRepository;
        this.domainObjectPagination = new DomainObjectPaginationImpl<>(
                exerciseSourceRepository, ExerciseSourceListElement::from);
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
    public @NonNull PaginatedDto<ExerciseSourceListElement> findAll(@NonNull ExerciseSourcePagingRequest pagingRequest) {
        return getDomainObjectPagination().findAll(
                pagingRequest,
                buildSpecification(
                        pagingRequest.getUserId(),
                        pagingRequest.getAccessOpsFilter(),
                        pagingRequest.getNameFilter(),
                        pagingRequest.getPublishedFilter()
                )
        );
    }

    protected @NonNull DomainObjectPagination<DbExerciseSource, ExerciseSourceListElement> getDomainObjectPagination() {
        return domainObjectPagination;
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

    private static Specification<DbExerciseSource> buildSpecification(
            OptionalField<String> userIdFilter,
            OptionalField<Set<AccessOp>> accessOpsFilter,
            OptionalField<String> nameFilter,
            OptionalField<Boolean> publishedFilter
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (userIdFilter.isPresent() || accessOpsFilter.isPresent()) {
                Join<DbExerciseSource, DbExerciseSourceAccess> join = root.join(DbExerciseSource.Fields.exerciseSourceAccesses);

                if (userIdFilter.isPresent()) {
                    predicates.add(getUserIdFilterPredicate(join, cb, userIdFilter.get()));
                }

                if (accessOpsFilter.isPresent()) {
                    predicates.add(getAccessOpsFilterPredicate(join, cb, accessOpsFilter.get()));
                }
            }

            if (nameFilter.isPresent()) {
                predicates.add(getNameFilter(root, cb, nameFilter.get()));
            }

            if (publishedFilter.isPresent()) {
                predicates.add(getPublishedFilter(root, cb, Objects.requireNonNull(publishedFilter.get())));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate getUserIdFilterPredicate(Join<DbExerciseSource, DbExerciseSourceAccess> join,
                                                      CriteriaBuilder cb,
                                                      String userId) {
        Path<Object> userIdPath = join.get(DbExerciseSourceAccess.Fields.userId);
        return userId != null ? cb.equal(userIdPath, userId) : cb.isNull(userIdPath);
    }

    private static Predicate getAccessOpsFilterPredicate(Join<DbExerciseSource, DbExerciseSourceAccess> join,
                                                         CriteriaBuilder cb,
                                                         Set<AccessOp> accessOps) {
        Path<String> accessOpsPath = join.get(DbExerciseSourceAccess.Fields.accessOps);
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

    private static Predicate getNameFilter(Root<DbExerciseSource> root,
                                           CriteriaBuilder cb,
                                           String name) {
        Path<String> namePath = root.get(DbExerciseSource.Fields.name);
        return name != null
                ? cb.like(cb.upper(namePath), "%" + name.toUpperCase() + "%")
                : cb.isNull(namePath);
    }

    private static Predicate getPublishedFilter(Root<DbExerciseSource> root,
                                                CriteriaBuilder cb,
                                                boolean published) {
        Path<Boolean> publishedPath = root.get(DbExerciseSource.Fields.published);
        if (published) {
            return cb.isTrue(publishedPath);
        } else {
            return cb.isFalse(publishedPath);
        }
    }

}
