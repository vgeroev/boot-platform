import { ModuleRoute } from "../../../route/ModuleRoute";
import ArticleListPage from "../page/article-list/ArticleListPage";
import ArticlePage from "../page/article/ArticlePage";
import CreateArticlePage from "../page/create-article/CreateArticlePage";
import UpdateArticlePage from "../page/update-article/UpdateArticlePage";

export const getArticleRoute = (id: string | number) => {
  return `/maths-road-map/article/${id}`;
};

export const getCreateArticleRoute = () => {
  return `/maths-road-map/create-article`;
};

export const getArticleListRoute = () => {
  return `maths-road-map/article-list`;
};

export const getUpdateArticleRoute = (id: number | string) => {
  return `/maths-road-map/update-article/${id}`;
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
  {
    path: getUpdateArticleRoute(":id"),
    element: <UpdateArticlePage />,
  },
];

export { moduleRoutes };
