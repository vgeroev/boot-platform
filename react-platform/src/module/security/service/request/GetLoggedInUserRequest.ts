import {
  HttpCallerFactory,
  HttpRequestMethod,
} from "../../../../hook/useHttpHook";
import { UserWithPrivilegesModel } from "../../model/UserModelWithPrivileges";
import { SecurityBaseRequest } from "./SecurityBaseRequest";

export class GetLoggedInUserRequest extends SecurityBaseRequest<UserWithPrivilegesModel> {
  constructor(httpCallerFactory: HttpCallerFactory) {
    super(UserWithPrivilegesModel, httpCallerFactory);
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

  public static build(
    httpCallerFactory: HttpCallerFactory,
  ): GetLoggedInUserRequest {
    return new GetLoggedInUserRequest(httpCallerFactory);
  }
}
