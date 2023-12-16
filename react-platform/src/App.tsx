import React from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import Home from "./component/home/Home";
import BaseLayout from "./layout/BaseLayout";
import { moduleRoutes as exerciseModuleRoutes } from "./module/exercises/route/ExcercisesRouteGetter";
import { ModuleRoute } from "./route/ModuleRoute";

function getModuleRoutes(
  moduleRoutes: Array<ModuleRoute>,
): Array<React.ReactElement> {
  return moduleRoutes.map((route) => {
    let routeElement;
    if (route.withBaseLayout === false) {
      routeElement = route.element;
    } else {
      routeElement = <BaseLayout component={route.element} />;
    }
    return <Route path={route.path} element={routeElement} />;
  });
}

function App() {
  let routes: Array<React.ReactElement> = [];
  routes.push(
    <Route path="/home" element={<BaseLayout component={<Home />} />} />,
  );
  routes = routes.concat(getModuleRoutes(exerciseModuleRoutes));

  return (
    <BrowserRouter>
      <Routes>
        {routes}

        {/* <Route */}
        {/*   path="/exercises/exercise-source-list" */}
        {/*   element={<BaseLayout component={<ExerciseSourceList />} />} */}
        {/* /> */}
        {/* <Route */}
        {/*   path="/exercises/:exerciseSourceId/list" */}
        {/*   element={<BaseLayout component={<ExerciseList />} />} */}
        {/* /> */}
        {/* <Route */}
        {/*   path="/exercises/:exerciseSourceId/new" */}
        {/*   element={<BaseLayout component={<ExerciseNew />} />} */}
        {/* /> */}
        {/* <Route */}
        {/*   path="/exercises/:exerciseId" */}
        {/*   element={<BaseLayout component={<Exercise />} />} */}
        {/* /> */}
        {/* <Route path="*" element={<Navigate to="/home" />} /> */}
      </Routes>
    </BrowserRouter>
  );
}

export default App;
