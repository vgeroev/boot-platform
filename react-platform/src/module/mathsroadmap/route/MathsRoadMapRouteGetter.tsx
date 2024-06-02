import { ModuleRoute } from "../../../route/ModuleRoute";
import ArticleListPage from "../page/article-list/ArticleListPage";
import ArticlePage from "../page/article/ArticlePage";
import CreateArticlePage from "../page/create-article/CreateArticlePage";

export const getArticleRoute = (id: string | number) => {
  return `/maths-road-map/article/${id}`;
};

export const getCreateArticleRoute = () => {
  return `/maths-road-map/create-article`;
};

export const getArticleListRoute = () => {
  return `maths-road-map/article-list`;
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
  {
    path: getArticleListRoute(),
    element: <ArticleListPage />,
  },
];

export { moduleRoutes };
