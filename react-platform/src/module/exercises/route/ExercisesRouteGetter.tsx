import { ModuleRoute } from "../../../route/ModuleRoute";
import Exercise from "../exercise/Exercise";
import ExerciseList from "../exercise/ExerciseList";
import ExerciseNew from "../exercise/ExerciseNew";
import ExerciseSourceList from "../exercisesource/ExerciseSourceList";

const MODULE: string = "exercises";
const EXERCISE_SOURCE_LIST_PATH = `/${MODULE}/exercise-source-list`;
//-------------------------------------------------------------------------------------
const EXERCISE_LIST_PATH = `/${MODULE}/:exerciseSourceId`;
function getExerciseListPath(exerciseSourceId: number) {
  return `/${MODULE}/${exerciseSourceId}`;
}
//-------------------------------------------------------------------------------------
const EXERCISE_PATH = `/${MODULE}/exercise/:id`;
function getExercisePath(id: number) {
  return `/${MODULE}/exercise/${id}`;
}
//-------------------------------------------------------------------------------------
const EXERCISE_NEW_PATH = `/${MODULE}/:exerciseSourceId/new`;
function getCreateExercisePath(exerciseSourceId: number) {
  return `/${MODULE}/${exerciseSourceId}/new`;
}
//-------------------------------------------------------------------------------------

const moduleRoutes: Array<ModuleRoute> = [
  {
    path: EXERCISE_SOURCE_LIST_PATH,
    element: <ExerciseSourceList />,
  },
  {
    path: EXERCISE_LIST_PATH,
    element: <ExerciseList />,
  },
  {
    path: EXERCISE_PATH,
    element: <Exercise />,
  },
  {
    path: EXERCISE_NEW_PATH,
    element: <ExerciseNew />,
  },
];

export {
  moduleRoutes,
  EXERCISE_SOURCE_LIST_PATH,
  EXERCISE_LIST_PATH,
  getExerciseListPath,
  EXERCISE_PATH,
  getExercisePath,
  EXERCISE_NEW_PATH,
  getCreateExercisePath,
};
