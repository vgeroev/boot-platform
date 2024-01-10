package org.vmalibu.module.exercises.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vmalibu.module.exercises.database.dao.ExerciseRepository;
import org.vmalibu.module.exercises.database.domainobject.DbExercise;
import org.vmalibu.module.exercises.database.domainobject.DbExerciseSource;
import org.vmalibu.module.exercises.service.exercise.ExerciseDto;
import org.vmalibu.module.exercises.service.exercise.ExerciseServiceImpl;
import org.vmalibu.module.exercises.service.exercise.ExerciseSolutionStatus;
import org.vmalibu.module.exercises.utils.TestUtils;

import java.util.Date;
import java.util.Optional;
import java.util.Random;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceImplTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private ExerciseServiceImpl exerciseService;

    @Test
    @DisplayName("Test Case: Get exercise by invalid id. Awaiting null")
    void getExerciseByInvalidIdTest() {
        when(exerciseRepository.findById(anyLong())).thenReturn(Optional.empty());

        //--------------------------------------------------------------------------------------------------------------

        long invalidId = 12345L;
        Assertions.assertThat(exerciseService.get(invalidId)).isNull();

        //--------------------------------------------------------------------------------------------------------------

        verify(exerciseRepository, only()).findById(invalidId);
    }

    @Test
    @DisplayName("Test Case: Get exercise by id. Awaiting correct answer")
    void getExerciseByTest() {
        long id = new Random().nextLong();
        Date createdAt = new Date();
        Date updatedAt = new Date();
        String problemName = RandomStringUtils.randomAlphabetic(10);
        String problem = RandomStringUtils.randomAlphabetic(100);
        ExerciseSolutionStatus solutionStatus = TestUtils.getRandomEnumValue(ExerciseSolutionStatus.class);
        String solution = RandomStringUtils.randomAlphabetic(100);
        long exerciseSourceId = new Random().nextLong();

        DbExercise exercise = mock(DbExercise.class);
        when(exercise.getId()).thenReturn(id);
        when(exercise.getCreatedAt()).thenReturn(createdAt);
        when(exercise.getUpdatedAt()).thenReturn(updatedAt);
        when(exercise.getProblemName()).thenReturn(problemName);
        when(exercise.getProblem()).thenReturn(problem);
        when(exercise.getSolutionStatus()).thenReturn(solutionStatus);
        when(exercise.getSolution()).thenReturn(solution);

        DbExerciseSource exerciseSource = mock(DbExerciseSource.class);
        when(exerciseSource.getId()).thenReturn(exerciseSourceId);
        when(exercise.getExerciseSource()).thenReturn(exerciseSource);

        when(exerciseRepository.findById(anyLong())).thenReturn(Optional.of(exercise));

        //--------------------------------------------------------------------------------------------------------------

        Assertions.assertThat(exerciseService.get(id)).isNotNull()
                .returns(id, ExerciseDto::id)
                .returns(createdAt, ExerciseDto::createdAt)
                .returns(updatedAt, ExerciseDto::updatedAt)
                .returns(problemName, ExerciseDto::problemName)
                .returns(problem, ExerciseDto::problem)
                .returns(solutionStatus, ExerciseDto::solutionStatus)
                .returns(solution, ExerciseDto::solution)
                .returns(exerciseSourceId, ExerciseDto::exerciseSourceId);

        //--------------------------------------------------------------------------------------------------------------

        verify(exerciseRepository, only()).findById(id);
    }


}
