import {
  HttpCallerFactory,
  HttpRequestMethod,
} from "../../../../hook/useHttpHook";
import { RoadMapTreeModel } from "../../model/RoadMapTreeModel";
import { MathsRoadMapBaseRequest } from "./MathsRoadMapBaseRequest";

export interface RoadMapReplaceTreeRequestVariables {
  id: number;
}

export interface ArticleEdge {
  prevId: number;
  nextId: number;
}

export interface RoadMapReplaceTreeRequestData {
  tree: Array<ArticleEdge>;
}

export class ReplaceRoadMapTreeRequest extends MathsRoadMapBaseRequest<
  RoadMapTreeModel,
  RoadMapReplaceTreeRequestData,
  RoadMapReplaceTreeRequestVariables
> {
  constructor(httpCallerFactory: HttpCallerFactory) {
    super(RoadMapTreeModel, httpCallerFactory);
  }

  public isAuthorized(): boolean {
    return true;
  }

  public getHttpRequestMethod(): HttpRequestMethod {
    return "patch";
  }

  public getRelativeApiPath(
    requstVariables?: RoadMapReplaceTreeRequestVariables,
  ): string {
    return `road-map/replace-tree/${requstVariables?.id}`;
  }

  public static build(
    httpCallerFactory: HttpCallerFactory,
  ): ReplaceRoadMapTreeRequest {
    return new ReplaceRoadMapTreeRequest(httpCallerFactory);
  }
}
