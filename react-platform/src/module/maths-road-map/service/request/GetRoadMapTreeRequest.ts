import {
  HttpCallerFactory,
  HttpRequestMethod,
} from "../../../../hook/useHttpHook";
import { RoadMapTreeModel } from "../../model/RoadMapTreeModel";
import { MathsRoadMapBaseRequest } from "./MathsRoadMapBaseRequest";

export interface GetRoadMapTreeVariables {
  id: number;
}

export class GetRoadMapTreeRequest extends MathsRoadMapBaseRequest<
  RoadMapTreeModel,
  any,
  GetRoadMapTreeVariables
> {
  constructor(httpCallerFactory: HttpCallerFactory) {
    super(RoadMapTreeModel, httpCallerFactory, false);
  }

  public getHttpRequestMethod(): HttpRequestMethod {
    return "get";
  }

  public getRelativeApiPath(requstVariables?: GetRoadMapTreeVariables): string {
    return `road-map/tree/${requstVariables?.id}`;
  }

  public static build(
    httpCallerFactory: HttpCallerFactory,
  ): GetRoadMapTreeRequest {
    return new GetRoadMapTreeRequest(httpCallerFactory);
  }
}
