import { getAnonPlatformUrl } from "./UrlUtils";

export interface HttpRequest {
  method: "get" | "post" | "delete" | "patch" | "put";
  url: string;
}

export function getModulesRequest(): HttpRequest {
  return {
    method: "get",
    url: getAnonPlatformUrl() + "/modules",
  };
}
