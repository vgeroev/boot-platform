import {
  HttpCallerFactory,
  HttpRequestMethod,
} from "../../../../hook/useHttpHook";
import { RoadMapModel } from "../../model/RoadMapModel";
import { MathsRoadMapBaseRequest } from "./MathsRoadMapBaseRequest";

export interface CreateRoadMapRequestData {
  title: string;
  description: string | null;
}

export class CreateRoadMapRequest extends MathsRoadMapBaseRequest<
  RoadMapModel,
  CreateRoadMapRequestData
> {
  constructor(httpCallerFactory: HttpCallerFactory) {
    super(RoadMapModel, httpCallerFactory, true);
  }

  public getHttpRequestMethod(): HttpRequestMethod {
    return "post";
  }
  public getRelativeApiPath(): string {
    return "road-map";
  }

  public static build(
    httpCallerFactory: HttpCallerFactory,
  ): CreateRoadMapRequest {
    return new CreateRoadMapRequest(httpCallerFactory);
  }
}
