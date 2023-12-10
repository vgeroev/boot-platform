import { BrowserRouter, Route, Routes } from "react-router-dom";
import { Home } from "./component";
import Exercise from "./component/exercises/exercise/Exercise";
import ExerciseList from "./component/exercises/exercise/ExerciseList";
import ExerciseNew from "./component/exercises/exercise/ExerciseNew";
import ExerciseSourceList from "./component/exercises/exercisesource/ExerciseSourceList";
import BaseLayout from "./layout/BaseLayout";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/home" element={<BaseLayout component={<Home />} />} />
        <Routes
          path="/exercises/exercise-source-list"
          element={<BaseLayout component={<ExerciseSourceList />} />}
        />
        <Route
          path="/exercises/:exerciseSourceId/list"
          element={<BaseLayout component={<ExerciseList />} />}
        />
        <Route
          path="/exercises/:exerciseSourceId/new"
          element={<BaseLayout component={<ExerciseNew />} />}
        />
        <Route
          path="/exercises/:exerciseId"
          element={<BaseLayout component={<Exercise />} />}
        />
        {/* <Route path="*" element={<Navigate to="/home" />} /> */}
      </Routes>
    </BrowserRouter>
  );
}

export default App;
