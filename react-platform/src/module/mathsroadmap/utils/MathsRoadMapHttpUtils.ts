import { HttpRequest } from "../../../utils/HttpUtils";
import { getAuthorizedUrl } from "../../../utils/UrlUtils";

const MATHS_ROAD_MAP_AUTHORIZED_URL = getAuthorizedUrl("maths-road-map", "v1");

function getMathsRoadMapAuthorizedUrl(path: string) {
  return MATHS_ROAD_MAP_AUTHORIZED_URL + path;
}

//---------------------------------------------------------------------------

const MATHS_ROAD_MAP_ARTICLE_PREVIEW_HTTP_REQUEST: HttpRequest = {
  method: "post",
  url: getMathsRoadMapAuthorizedUrl("/article/tex4ht/preview"),
};

const MATHS_ROAD_MAP_CREATE_ARTICLE_HTTP_REQUEST: HttpRequest = {
  method: "post",
  url: getMathsRoadMapAuthorizedUrl("/article"),
};
export {
  MATHS_ROAD_MAP_ARTICLE_PREVIEW_HTTP_REQUEST,
  MATHS_ROAD_MAP_CREATE_ARTICLE_HTTP_REQUEST,
};
