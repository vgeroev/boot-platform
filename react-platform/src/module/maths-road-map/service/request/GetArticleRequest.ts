import {
  HttpCallerFactory,
  HttpRequestMethod,
} from "../../../../hook/useHttpHook";
import { GetArticleModel } from "../../model/GetArticleModel";
import { MathsRoadMapBaseRequest } from "./MathsRoadMapBaseRequest";

export interface GetArticleRequestVariables {
  id: number;
}

export class GetArticleRequest extends MathsRoadMapBaseRequest<
  GetArticleModel,
  any,
  GetArticleRequestVariables
> {
  constructor(httpCallerFactory: HttpCallerFactory) {
    super(GetArticleModel, httpCallerFactory, false);
  }

  public getHttpRequestMethod(): HttpRequestMethod {
    return "get";
  }

  public getRelativeApiPath(
    requstVariables?: GetArticleRequestVariables,
  ): string {
    return `article/${requstVariables?.id}`;
  }

  public static build(httpCallerFactory: HttpCallerFactory): GetArticleRequest {
    return new GetArticleRequest(httpCallerFactory);
  }
}
