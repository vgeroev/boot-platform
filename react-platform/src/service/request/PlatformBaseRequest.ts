import { BaseModel } from "../../model/BaseModel";
import { BaseRequest } from "./BaseRequest";

export abstract class PlatformBaseRequest<
  M extends BaseModel | {} = {},
  D = any,
> extends BaseRequest<M, D> {
  public getApiVersion(): string {
    return "v1";
  }

  public getModule(): string {
    return "platform";
  }
}
