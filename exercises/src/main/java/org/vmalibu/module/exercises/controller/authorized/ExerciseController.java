package org.vmalibu.module.exercises.controller.authorized;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.vmalibu.module.exercises.ExercisesModuleConsts;
import org.vmalibu.module.exercises.access.ExercisesPrivilege;
import org.vmalibu.module.exercises.database.domainobject.DbExercise;
import org.vmalibu.module.exercises.database.domainobject.DbExerciseSource;
import org.vmalibu.module.exercises.exception.ExercisesExceptionFactory;
import org.vmalibu.module.exercises.service.exercise.*;
import org.vmalibu.module.exercises.service.exercise.list.ExerciseListElement;
import org.vmalibu.module.exercises.service.exercise.list.ExercisePagingRequest;
import org.vmalibu.module.exercises.service.exercise.list.ExerciseSortField;
import org.vmalibu.module.exercises.service.exercisesource.*;
import org.vmalibu.module.exercises.service.exercisesource.list.ExerciseSourceListElement;
import org.vmalibu.module.exercises.service.exercisesource.list.ExerciseSourcePagingRequest;
import org.vmalibu.module.exercises.service.exercisesource.list.ExerciseSourceSortField;
import org.vmalibu.module.exercises.service.exercisesourceaccess.ExerciseSourceAccessService;
import org.vmalibu.module.exercises.service.exercisesourcepublishrequest.ExerciseSourcePublishRequestCreationBuilder;
import org.vmalibu.module.exercises.service.exercisesourcepublishrequest.ExerciseSourcePublishRequestDto;
import org.vmalibu.module.exercises.service.exercisesourcepublishrequest.ExerciseSourcePublishRequestService;
import org.vmalibu.module.exercises.service.exercisesourcepublishrequest.ExerciseSourcePublishRequestStatus;
import org.vmalibu.module.security.access.AccessOp;
import org.vmalibu.module.security.authorization.controller.privilege.AccessPermission;
import org.vmalibu.module.security.authorization.controller.privilege.PrivilegeAccess;
import org.vmalibu.module.security.authorization.source.UserSource;
import org.vmalibu.modules.database.paging.PaginatedDto;
import org.vmalibu.modules.database.paging.PaginationForm;
import org.vmalibu.modules.module.exception.GeneralExceptionBuilder;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.utils.OptionalField;

import java.util.EnumSet;
import java.util.Map;

@RestController
@RequestMapping(ExercisesModuleConsts.REST_AUTHORIZED_PREFIX)
public class ExerciseController {

    protected static final String ID = "id";
    protected static final String EXERCISE_SOURCE_ID = "exerciseSourceId";

    private final ExerciseSourceService exerciseSourceService;
    private final ExerciseSourceAccessService exerciseSourceAccessService;
    private final ExerciseService exerciseService;
    private final ExerciseSourcePublishRequestService exerciseSourcePublishRequestService;

    @Autowired
    public ExerciseController(@NonNull ExerciseSourceService exerciseSourceService,
                              @NonNull ExerciseSourceAccessService exerciseSourceAccessService,
                              @NonNull ExerciseService exerciseService,
                              @NonNull ExerciseSourcePublishRequestService exerciseSourcePublishRequestService) {
        this.exerciseSourceService = exerciseSourceService;
        this.exerciseSourceAccessService = exerciseSourceAccessService;
        this.exerciseService = exerciseService;
        this.exerciseSourcePublishRequestService = exerciseSourcePublishRequestService;
    }

    @GetMapping("/exercise-source/list")
    @ResponseStatus(HttpStatus.OK)
    public PaginatedDto<ExerciseSourceListElement> exerciseSourceList(
            final UserSource userSource,
            @RequestParam(required = false) final Map<String, String> params
    ) throws PlatformException {
        ExerciseSourcePaginationForm form = new ExerciseSourcePaginationForm(params);
        return exerciseSourceService.findAll(
                new ExerciseSourcePagingRequest.Builder(form.page, form.pageSize)
                        .withSort(form.sortField, form.sortDirection)
                        .withUserId(OptionalField.of(userSource.getUserId()))
                        .withNameFilter(form.nameFilter)
                        .withAccessOpsFilter(OptionalField.of(EnumSet.of(AccessOp.READ)))
                        .build()
        );
    }

    @GetMapping("/exercise-source/published-list")
    @ResponseStatus(HttpStatus.OK)
    public PaginatedDto<ExerciseSourceListElement> exerciseSourcePublishedList(
            @RequestParam(required = false) final Map<String, String> params
    ) throws PlatformException {
        ExerciseSourcePaginationForm form = new ExerciseSourcePaginationForm(params);
        return exerciseSourceService.findAll(
                new ExerciseSourcePagingRequest.Builder(form.page, form.pageSize)
                        .withSort(form.sortField, form.sortDirection)
                        .withNameFilter(form.nameFilter)
                        .withPublishedFilter(OptionalField.of(true))
                        .build()
        );
    }

    @PostMapping("/exercise-source")
    @ResponseStatus(HttpStatus.CREATED)
    public ExerciseSourceDto createExerciseSource(
            @RequestBody final ExerciseSourceCreationForm form,
            final UserSource userSource
    ) throws PlatformException {
        return exerciseSourceService.create(
                new ExerciseSourceBuilder()
                        .name(form.name)
                        .ownerId(userSource.getUserId())
        );
    }

    @PatchMapping("/exercise-source/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ExerciseSourceDto update(
            @PathVariable(name = ID) final long id,
            @RequestBody final ExerciseSourceUpdateForm form,
            final UserSource userSource
    ) throws PlatformException {
        checkUserAccess(userSource.getUserId(), id, AccessOp.WRITE);
        return exerciseSourceService.update(id, new ExerciseSourceBuilder().name(form.name));
    }

    @DeleteMapping("/exercise-source/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExerciseSource(
            @PathVariable(name = ID) final long id,
            final UserSource userSource
    ) throws PlatformException {
        checkUserAccess(userSource.getUserId(), id, AccessOp.DELETE);
        exerciseSourceService.delete(id);
    }

    @GetMapping("/{exerciseSourceId}/exercise/list")
    @ResponseStatus(HttpStatus.OK)
    public PaginatedDto<ExerciseListElement> exerciseList(
            @PathVariable(name = EXERCISE_SOURCE_ID) final long exerciseSourceId,
            @RequestParam(required = false) final Map<String, String> params,
            final UserSource userSource
    ) throws PlatformException {
        ExercisePaginationForm form = new ExercisePaginationForm(params);

        checkIfPublishedOrUserHasAccess(userSource.getUserId(), exerciseSourceId, AccessOp.READ);
        return exerciseService.findAll(
                new ExercisePagingRequest.Builder(form.page, form.pageSize)
                        .withExerciseSourceId(exerciseSourceId)
                        .withSort(form.sortField, form.sortDirection)
                        .withProblemNameFilter(form.problemNameFilter)
                        .withSolutionStatusFilter(form.solutionStatusFilter)
                        .build()
        );
    }

    @PostMapping("/{exerciseSourceId}/exercise")
    @ResponseStatus(HttpStatus.CREATED)
    public ExerciseDto createExercise(
            @PathVariable(name = EXERCISE_SOURCE_ID) final long exerciseSourceId,
            @RequestBody final ExerciseCreationForm form,
            final UserSource userSource
    ) throws PlatformException {
        checkUserAccess(userSource.getUserId(), exerciseSourceId, AccessOp.WRITE);
        return exerciseService.create(
                new ExerciseBuilder()
                        .problemName(form.problemName)
                        .problem(form.problem)
                        .solutionStatus(form.solutionStatus)
                        .solution(form.solution)
                        .exerciseSourceId(exerciseSourceId)
        );
    }

    @GetMapping("/exercise/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ExerciseDto getExercise(
            @PathVariable(name = ID) final long id,
            final UserSource userSource
    ) throws PlatformException {
        ExerciseDto exercise = exerciseService.get(id);
        if (exercise == null) {
            throw GeneralExceptionBuilder.buildNotFoundDomainObjectException(DbExercise.class, id);
        }

        checkIfPublishedOrUserHasAccess(userSource.getUserId(), exercise.exerciseSourceId(), AccessOp.READ);
        return exercise;
    }

    @PatchMapping("/exercise/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ExerciseDto updateExercise(
            @PathVariable(ID) final long id,
            @RequestBody final ExerciseUpdateForm form,
            final UserSource userSource
    ) throws PlatformException {
        ExerciseDto exercise = exerciseService.get(id);
        if (exercise == null) {
            throw GeneralExceptionBuilder.buildNotFoundDomainObjectException(DbExercise.class, id);
        }

        checkUserAccess(userSource.getUserId(), exercise.exerciseSourceId(), AccessOp.WRITE);
        ExerciseBuilder builder = new ExerciseBuilder();
        form.problemName.ifPresent(builder::problemName);
        form.problem.ifPresent(builder::problem);
        form.solutionStatus.ifPresent(builder::solutionStatus);
        form.solution.ifPresent(builder::solution);

        return exerciseService.update(id, builder);
    }

    @PostMapping("/exercise-source/{id}/publish-request")
    @ResponseStatus(HttpStatus.OK)
    public ExerciseSourcePublishRequestDto publishExerciseSourceRequest(
            @PathVariable(ID) final long id,
            final UserSource userSource
    ) throws PlatformException {
        return exerciseSourcePublishRequestService.createRequest(
                new ExerciseSourcePublishRequestCreationBuilder()
                        .withExerciseSourceId(id)
                        .withUserId(userSource.getUserId())
        );
    }

    @PostMapping("/exercise-source/{id}/publish")
    @ResponseStatus(HttpStatus.OK)
    @AccessPermission(
            values = @PrivilegeAccess(
                    privilege = ExercisesPrivilege.class,
                    ops = { AccessOp.WRITE }
            )
    )
    public ExerciseSourcePublishRequestDto publishExerciseSource(
            @PathVariable(ID) final long id,
            @RequestBody final ExerciseSourcePublishForm form
    ) throws PlatformException {
        return exerciseSourcePublishRequestService.agreeOnRequest(id, form.status);
    }


    private void checkUserAccess(String userId, long exerciseSourceId, AccessOp... accessOps) throws PlatformException {
        boolean hasAccess = exerciseSourceAccessService.hasAccess(userId, exerciseSourceId, accessOps);
        if (!hasAccess) {
            throw ExercisesExceptionFactory.buildAccessDeniedOnExerciseSourceException(exerciseSourceId, userId);
        }
    }

    private void checkIfPublishedOrUserHasAccess(String userId, long exerciseSourceId, AccessOp... accessOps) throws PlatformException {
        ExerciseSourceDto exerciseSourceDto = exerciseSourceService.get(exerciseSourceId);
        if (exerciseSourceDto == null) {
            throw GeneralExceptionBuilder.buildNotFoundDomainObjectException(DbExerciseSource.class, exerciseSourceId);
        }

        if (!exerciseSourceDto.published()) {
            checkUserAccess(userId, exerciseSourceId, accessOps);
        }
    }

    public static class ExerciseSourcePaginationForm extends PaginationForm {

        static final String JSON_NAME_FILTER = "nameFilter";

        final ExerciseSourceSortField sortField;
        final OptionalField<String> nameFilter;

        public ExerciseSourcePaginationForm(@NonNull Map<String, String> params) throws PlatformException {
            super(params);

            if (params.containsKey(JSON_SORT_FIELD)) {
                this.sortField = parseEnum(ExerciseSourceSortField.class, params, JSON_SORT_FIELD);
            } else {
                this.sortField = null;
            }

            if (params.containsKey(JSON_NAME_FILTER)) {
                this.nameFilter = OptionalField.of(params.get(JSON_NAME_FILTER));
            } else {
                this.nameFilter = OptionalField.empty();
            }
        }

        @Override
        protected int getMaxPageSize() {
            return 512;
        }
    }

    public static class ExerciseSourceCreationForm {

        static final String JSON_NAME = "name";

        @JsonProperty(JSON_NAME)
        String name;

    }

    @Data
    public static class ExerciseSourceUpdateForm {

        static final String JSON_NAME = "name";

        @JsonProperty(JSON_NAME)
        String name;

    }

    public static class ExercisePaginationForm extends PaginationForm {

        static final String JSON_PROBLEM_NAME_FILTER = "problemNameFilter";
        static final String JSON_SOLUTION_STATUS_FILTER = "solutionStatusFilter";

        final ExerciseSortField sortField;
        final OptionalField<String> problemNameFilter;
        final OptionalField<ExerciseSolutionStatus> solutionStatusFilter;

        public ExercisePaginationForm(@NonNull Map<String, String> params) throws PlatformException {
            super(params);

            if (params.containsKey(JSON_SORT_FIELD)) {
                this.sortField = parseEnum(ExerciseSortField.class, params, JSON_SORT_FIELD);
            } else {
                this.sortField = null;
            }

            if (params.containsKey(JSON_PROBLEM_NAME_FILTER)) {
                this.problemNameFilter = OptionalField.of(params.get(JSON_PROBLEM_NAME_FILTER));
            } else {
                this.problemNameFilter = OptionalField.empty();
            }

            if (params.containsKey(JSON_SOLUTION_STATUS_FILTER)) {
                this.solutionStatusFilter = OptionalField.of(
                        parseEnum(ExerciseSolutionStatus.class, params, JSON_SOLUTION_STATUS_FILTER));
            } else {
                this.solutionStatusFilter = OptionalField.empty();
            }
        }

        @Override
        protected int getMaxPageSize() {
            return 1024;
        }
    }

    public static class ExerciseCreationForm {

        static final String JSON_PROBLEM_NAME = "problemName";
        static final String JSON_PROBLEM = "problem";
        static final String JSON_SOLUTION_STATUS = "solutionStatus";
        static final String JSON_SOLUTION = "solution";

        @JsonProperty(JSON_PROBLEM_NAME)
        String problemName;
        @JsonProperty(JSON_PROBLEM)
        String problem;
        @JsonProperty(JSON_SOLUTION_STATUS)
        ExerciseSolutionStatus solutionStatus;
        @JsonProperty(JSON_SOLUTION)
        String solution;

    }

    public static class ExerciseUpdateForm {

        static final String JSON_PROBLEM_NAME = "problemName";
        static final String JSON_PROBLEM = "problem";
        static final String JSON_SOLUTION_STATUS = "solutionStatus";
        static final String JSON_SOLUTION = "solution";

        OptionalField<String> problemName = OptionalField.empty();
        OptionalField<String> problem = OptionalField.empty();
        OptionalField<ExerciseSolutionStatus> solutionStatus = OptionalField.empty();
        OptionalField<String> solution = OptionalField.empty();

        @JsonProperty(JSON_PROBLEM_NAME)
        void setProblemName(String problemName) {
            this.problemName = OptionalField.of(problemName);
        }

        @JsonProperty(JSON_PROBLEM)
        void setProblem(String problem) {
            this.problem = OptionalField.of(problem);
        }

        @JsonProperty(JSON_SOLUTION_STATUS)
        void setSolutionStatus(ExerciseSolutionStatus solutionStatus) {
            this.solutionStatus = OptionalField.of(solutionStatus);
        }

        @JsonProperty(JSON_SOLUTION)
        void setSolution(String solution) {
            this.solution = OptionalField.of(solution);
        }
    }

    @Data
    public static class ExerciseSourcePublishForm {

        static final String JSON_STATUS = "status";

        @JsonProperty(JSON_STATUS)
        ExerciseSourcePublishRequestStatus status;

    }


}
