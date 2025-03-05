import {
  HttpCallerFactory,
  HttpRequestMethod,
} from "../../../hook/useHttpHook";
import { EmptyModel } from "../../../model/EmptyModel";
import { CoreBaseRequest } from "./CoreBaseRequest";

export class RemoveTagRequest extends CoreBaseRequest<EmptyModel, number[]> {
  constructor(httpCallerFactory: HttpCallerFactory) {
    super(EmptyModel, httpCallerFactory);
  }

  public isAuthorized(): boolean {
    return true;
  }

  public getHttpRequestMethod(): HttpRequestMethod {
    return "delete";
  }
  public getRelativeApiPath(): string {
    return "tag/remove";
  }

  public static build(httpCallerFactory: HttpCallerFactory): RemoveTagRequest {
    return new RemoveTagRequest(httpCallerFactory);
  }
}
