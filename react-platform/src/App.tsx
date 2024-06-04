import React from "react";
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import NotFound from "./component/notfound/NotFound";
import BaseLayout from "./layout/BaseLayout";
import { moduleRoutes as exerciseModuleRoutes } from "./module/exercises/route/ExercisesRouteGetter";
import { moduleRoutes as mathsRoadMapModuleRoutes } from "./module/mathsroadmap/route/MathsRoadMapRouteGetter";
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
    <Route path="/" element={<Navigate to="/home" replace />} />,
  );
  routes = routes
    .concat(getModuleRoutes(exerciseModuleRoutes))
    .concat(getModuleRoutes(mathsRoadMapModuleRoutes));

  routes.push(
    <Route path="/authorization-redirect" element={<></>} />,
    <Route path="*" element={<BaseLayout component={<NotFound />} />} />,
  );

  return (
    <BrowserRouter>
      <Routes>{routes}</Routes>
    </BrowserRouter>
  );
}

export default App;
