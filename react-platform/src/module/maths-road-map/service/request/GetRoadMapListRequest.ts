import {
  HttpCallerFactory,
  HttpRequestMethod,
} from "../../../../hook/useHttpHook";
import { RoadMapListModel } from "../../model/RoadMapListModel";
import { MathsRoadMapBaseRequest } from "./MathsRoadMapBaseRequest";

export class GetRoadMapListRequest extends MathsRoadMapBaseRequest<RoadMapListModel> {
  constructor(httpCallerFactory: HttpCallerFactory) {
    super(RoadMapListModel, httpCallerFactory);
  }

  public isAuthorized(): boolean {
    return false;
  }

  public getHttpRequestMethod(): HttpRequestMethod {
    return "get";
  }

  public getRelativeApiPath(): string {
    return `road-map/list`;
  }

  public static build(
    httpCallerFactory: HttpCallerFactory,
  ): GetRoadMapListRequest {
    return new GetRoadMapListRequest(httpCallerFactory);
  }
}
