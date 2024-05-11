import { BaseModel } from "../../../../model/BaseModel";
import { BaseRequest } from "../../../../service/request/BaseRequest";

export abstract class MathsRoadMapBaseRequest<
  M extends BaseModel,
  D = any,
> extends BaseRequest<M, D> {
  public getApiVersion(): string {
    return "v1";
  }

  public getModule(): string {
    return "maths-road-map";
  }
}
