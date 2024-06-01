import { ModuleRoute } from "../../../route/ModuleRoute";
import ArticlePage from "../page/article/ArticlePage";
import CreateArticlePage from "../page/create-article/CreateArticlePage";

export const getArticleRoute = (id: string) => {
  return `/maths-road-map/article/${id}`;
};

export const getCreateArticleRoute = () => {
  return `/maths-road-map/create-article`;
};

const moduleRoutes: Array<ModuleRoute> = [
  {
    path: getCreateArticleRoute(),
    element: <CreateArticlePage />,
  },
  {
    path: getArticleRoute(":id"),
    element: <ArticlePage />,
  },
];

export { moduleRoutes };
