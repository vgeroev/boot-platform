import {
  HttpCallerFactory,
  HttpRequestMethod,
} from "../../../../hook/useHttpHook";
import { UserModel } from "../../model/UserModel";
import { SecurityBaseRequest } from "./SecurityBaseRequest";

export class GetLoggedInUser extends SecurityBaseRequest<UserModel> {
  constructor(httpCallerFactory: HttpCallerFactory) {
    super(UserModel, httpCallerFactory, true);
  }

  public isAuthorized(): boolean {
    return true;
  }

  public getHttpRequestMethod(): HttpRequestMethod {
    return "get";
  }
  public getRelativeApiPath(): string {
    return "user/logged-in";
  }

  public static build(httpCallerFactory: HttpCallerFactory): GetLoggedInUser {
    return new GetLoggedInUser(httpCallerFactory);
  }
}
