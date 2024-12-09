import {
  HttpCallerFactory,
  HttpRequestMethod,
} from "../../../../hook/useHttpHook";
import { EmptyModel } from "../../../../model/EmptyModel";
import { SecurityBaseRequest } from "./SecurityBaseRequest";

export interface LoginForm {
  username: string;
  password: string;
}

export class LoginRequest extends SecurityBaseRequest<EmptyModel, LoginForm> {
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
    return "login";
  }

  public static build(httpCallerFactory: HttpCallerFactory): LoginRequest {
    return new LoginRequest(httpCallerFactory);
  }
}
