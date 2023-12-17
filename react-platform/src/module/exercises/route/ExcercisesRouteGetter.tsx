import { ModuleRoute } from "../../../route/ModuleRoute";
import ExerciseList from "../exercise/ExerciseList";
import ExerciseSourceList from "../exercisesource/ExerciseSourceList";

const MODULE: string = "exercises";
const EXERCISE_SOURCE_LIST_PATH = `/${MODULE}/exercise-source-list`;
const EXERCISE_LIST_PATH = `/${MODULE}/:exerciseSourceId`;

function getExerciseListPath(exerciseSourceId: number) {
  return `/${MODULE}/${exerciseSourceId}`;
}

const moduleRoutes: Array<ModuleRoute> = [
  {
    path: EXERCISE_SOURCE_LIST_PATH,
    element: <ExerciseSourceList />,
  },
  {
    path: EXERCISE_LIST_PATH,
    element: <ExerciseList />,
  },
];

export {
  moduleRoutes,
  EXERCISE_SOURCE_LIST_PATH,
  EXERCISE_LIST_PATH,
  getExerciseListPath,
};
