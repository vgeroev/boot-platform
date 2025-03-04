import React from "react";
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import NotFound from "./component/notfound/NotFound";
import { UserProvider } from "./hook/UserContext";
import BaseLayout from "./layout/BaseLayout";
import { moduleRoutes as mathsRoadMapModuleRoutes } from "./module/maths-road-map/route/MathsRoadMapRouteGetter";
import { moduleRoutes as securityRoutes } from "./module/security/route/SecurityRouteGetter";
import { moduleRoutes as coreRoutes } from "./module/core/route/CoreRouteGetter";
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
  const homeRoute = "/home";
  routes.push(
    <Route path={homeRoute} element={<BaseLayout component={<Home />} />} />,
    <Route path="/" element={<Navigate to={homeRoute} replace />} />,
  );
  routes = routes
    .concat(getModuleRoutes(securityRoutes))
    .concat(getModuleRoutes(coreRoutes))
    .concat(getModuleRoutes(mathsRoadMapModuleRoutes));

  routes.push(
    <Route path="*" element={<BaseLayout component={<NotFound />} />} />,
  );

  return (
    <UserProvider>
      <BrowserRouter>
        <Routes>{routes}</Routes>
      </BrowserRouter>
    </UserProvider>
  );
}

export default App;
