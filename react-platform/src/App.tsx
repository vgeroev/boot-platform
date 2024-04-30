import React from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import BaseLayout from "./layout/BaseLayout";
import { moduleRoutes as exerciseModuleRoutes } from "./module/exercises/route/ExercisesRouteGetter";
import Home from "./page/home/Home";
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
      <Routes>{routes}</Routes>
    </BrowserRouter>
  );
}

export default App;
