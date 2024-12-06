import {
  HttpCallerFactory,
  HttpRequest,
  HttpRequestMethod,
} from "../../../../hook/useHttpHook";
import { EmptyModel } from "../../../../model/EmptyModel";
import { BaseRequest } from "../../../../service/request/BaseRequest";

export interface LoginForm {
  username: string;
  password: string;
}

export class LoginRequest extends BaseRequest<EmptyModel, any, LoginForm> {
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

  public getRelativeApiPath(requestVariables?: LoginForm | undefined): string {
    throw new Error("Method not implemented.");
  }

  public constructHttpRequest(requestVariables: LoginForm): HttpRequest {
    const username: string = requestVariables.username;
    const password: string = requestVariables.password;
    return {
      method: "post",
      url: `/authorized/login?username=${username}&password=${password}`,
    };
  }

  public static build(httpCallerFactory: HttpCallerFactory): LoginRequest {
    return new LoginRequest(httpCallerFactory);
  }
}
