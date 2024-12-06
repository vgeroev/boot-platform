import {
  HttpCallerFactory,
  HttpRequest,
  HttpRequestMethod,
} from "../../../../hook/useHttpHook";
import { EmptyModel } from "../../../../model/EmptyModel";
import { BaseRequest } from "../../../../service/request/BaseRequest";

export class LogoutRequest extends BaseRequest<EmptyModel, {}, {}> {
  constructor(httpCallerFactory: HttpCallerFactory) {
    super(EmptyModel, httpCallerFactory, false);
  }

  public getApiVersion(): string {
    throw new Error("Method not implemented.");
  }

  public getModule(): string {
    throw new Error("Method not implemented.");
  }

  public getHttpRequestMethod(): HttpRequestMethod {
    throw new Error("Method not implemented.");
  }

  public getRelativeApiPath(requestVariables?: {} | undefined): string {
    throw new Error("Method not implemented.");
  }

  public constructHttpRequest(requestVariables: {}): HttpRequest {
    return {
      method: "post",
      url: `/authorized/logout`,
    };
  }

  public static build(httpCallerFactory: HttpCallerFactory): LogoutRequest {
    return new LogoutRequest(httpCallerFactory);
  }
}
