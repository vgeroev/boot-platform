import { getAuthorizedUrl } from "../../../service/http";

export const getAuthorizedExercisesUrl = (path: string) =>
  getAuthorizedUrl("exercises") + path;
