import { ModuleRoute } from "../../../route/ModuleRoute";
import ExerciseSourceList from "../exercisesource/ExerciseSourceList";

const MODULE: string = "exercises";
const EXERCISE_SOURCE_LIST_PATH = `/${MODULE}/exercise-source-list`;

const moduleRoutes: Array<ModuleRoute> = [
  {
    path: EXERCISE_SOURCE_LIST_PATH,
    element: <ExerciseSourceList />,
  },
];

export { moduleRoutes, EXERCISE_SOURCE_LIST_PATH };
