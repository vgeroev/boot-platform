import { ModuleRoute } from "../../../route/ModuleRoute";
import TagsManagementPage from "../page/tags-management/TagsManagementPage";

export const getTagsPageRequest = () => {
  return `/core/tags`;
};

const moduleRoutes: Array<ModuleRoute> = [
  {
    path: getTagsPageRequest(),
    element: <TagsManagementPage />,
  },
];

export { moduleRoutes };
