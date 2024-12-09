import { HttpCallerFactory, HttpRequestMethod } from "../../hook/useHttpHook";
import { InitializedModulesModel } from "../../model/InitializedModulesModel";
import { PlatformBaseRequest } from "./PlatformBaseRequest";

export class InitializedModulesRequest extends PlatformBaseRequest<InitializedModulesModel> {
  constructor(httpCallerFactory: HttpCallerFactory) {
    super(InitializedModulesModel, httpCallerFactory);
  }

  public isAuthorized(): boolean {
    return false;
  }

  public getHttpRequestMethod(): HttpRequestMethod {
    return "get";
  }

  public getRelativeApiPath(): string {
    return "modules";
  }

  public static build(
    httpCallerFactory: HttpCallerFactory,
  ): InitializedModulesRequest {
    return new InitializedModulesRequest(httpCallerFactory);
  }
}
