import { BaseModel } from "../../../model/BaseModel";
import { BaseRequest } from "../../../service/request/BaseRequest";

export abstract class CoreBaseRequest<
  M extends BaseModel,
  D = any,
  R = any,
> extends BaseRequest<M, D, R> {
  public getApiVersion(): string {
    return "v1";
  }

  public getModule(): string {
    return "core";
  }
}
