import { ModuleRoute } from "../../../route/ModuleRoute";
import CreateArticlePage from "../page/create-article/CreateArticlePage";

const moduleRoutes: Array<ModuleRoute> = [
    {
        path: "/maths-road-map/create-article",
        element: <CreateArticlePage />,
    },
];

export { moduleRoutes };
