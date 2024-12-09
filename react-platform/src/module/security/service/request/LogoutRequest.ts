import {
  HttpCallerFactory,
  HttpRequestMethod,
} from "../../../../hook/useHttpHook";
import { EmptyModel } from "../../../../model/EmptyModel";
import { SecurityBaseRequest } from "./SecurityBaseRequest";

export class LogoutRequest extends SecurityBaseRequest<EmptyModel, {}, {}> {
  constructor(httpCallerFactory: HttpCallerFactory) {
    super(EmptyModel, httpCallerFactory);
  }

  public isAuthorized(): boolean {
    return true;
  }

  public getHttpRequestMethod(): HttpRequestMethod {
    return "post";
  }
  public getRelativeApiPath(): string {
    return "logout";
  }

  public static build(httpCallerFactory: HttpCallerFactory): LogoutRequest {
    return new LogoutRequest(httpCallerFactory);
  }
}
