import { ModuleRoute } from "../../../route/ModuleRoute";
import LoginPage from "../page/login/LoginPage";
import RegisterPage from "../page/register/RegisterPage";
import { getLoginRoute, getRegisterRoute } from "./SecurityRoutes";

const moduleRoutes: Array<ModuleRoute> = [
  {
    path: getRegisterRoute(),
    element: <RegisterPage />,
  },
  {
    path: getLoginRoute(),
    element: <LoginPage />,
  },
];

export { moduleRoutes };
