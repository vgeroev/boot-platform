import {
  HttpCallerFactory,
  HttpRequestMethod,
} from "../../../../hook/useHttpHook";
import { UserModel } from "../../model/UserModel";
import { SecurityBaseRequest } from "./SecurityBaseRequest";

export interface RegisterForm {
  username: string;
  password: string;
}

export class RegisterRequest extends SecurityBaseRequest<
  UserModel,
  RegisterForm
> {
  constructor(httpCallerFactory: HttpCallerFactory) {
    super(UserModel, httpCallerFactory, false);
  }

  public isAuthorized(): boolean {
    return false;
  }

  public getHttpRequestMethod(): HttpRequestMethod {
    return "post";
  }
  public getRelativeApiPath(): string {
    return "register";
  }

  public static build(httpCallerFactory: HttpCallerFactory): RegisterRequest {
    return new RegisterRequest(httpCallerFactory);
  }
}
