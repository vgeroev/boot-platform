import { ModuleRoute } from "../../../route/ModuleRoute";
import ArticleListPage from "../page/article-list/ArticleListPage";
import ArticlePage from "../page/article/ArticlePage";
import CreateArticlePage from "../page/create-article/CreateArticlePage";
import CreateRoadMapPage from "../page/create-road-map/CreateRoadMapPage";
import RoadMapListPage from "../page/road-map-list/RoadMapListPage";
import RoadMapPage from "../page/road-map/RoadMapPage";
import UpdateArticlePage from "../page/update-article/UpdateArticlePage";

export const getArticleRoute = (id: string | number) => {
  return `/maths-road-map/article/${id}`;
};

export const getCreateArticleRoute = () => {
  return `/maths-road-map/create-article`;
};

export const getArticleListRoute = () => {
  return `/maths-road-map/article-list`;
};

export const getUpdateArticleRoute = (id: number | string) => {
  return `/maths-road-map/update-article/${id}`;
};

export const getCreateRoadMapRoute = () => {
  return `/maths-road-map/create`;
};

export const getRoadMapListRoute = () => {
  return `/maths-road-map/list`;
};

export const getRoadMapRoute = (id: number | string) => {
  return `/maths-road-map/${id}`;
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
  {
    path: getCreateRoadMapRoute(),
    element: <CreateRoadMapPage />,
  },
  {
    path: getRoadMapListRoute(),
    element: <RoadMapListPage />,
  },
  {
    path: getRoadMapRoute(":id"),
    element: <RoadMapPage />,
  },
];

export { moduleRoutes };
