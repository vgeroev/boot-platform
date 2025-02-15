import {
  HttpCallerFactory,
  HttpRequestMethod,
} from "../../../../hook/useHttpHook";
import { ArticleLikeActionModel } from "../../model/ArticleLikeAction";
import { MathsRoadMapBaseRequest } from "./MathsRoadMapBaseRequest";

export interface GetArticleLikeActionRequestVariables {
  id: number;
}

export class GetArticleLikeActionRequest extends MathsRoadMapBaseRequest<
  ArticleLikeActionModel,
  any,
  GetArticleLikeActionRequestVariables
> {
  constructor(httpCallerFactory: HttpCallerFactory) {
    super(ArticleLikeActionModel, httpCallerFactory);
  }

  public isAuthorized(): boolean {
    return true;
  }

  public getHttpRequestMethod(): HttpRequestMethod {
    return "get";
  }

  public getRelativeApiPath(
    requstVariables?: GetArticleLikeActionRequestVariables,
  ): string {
    return `article/${requstVariables?.id}/like-action`;
  }

  public static build(
    httpCallerFactory: HttpCallerFactory,
  ): GetArticleLikeActionRequest {
    return new GetArticleLikeActionRequest(httpCallerFactory);
  }
}
