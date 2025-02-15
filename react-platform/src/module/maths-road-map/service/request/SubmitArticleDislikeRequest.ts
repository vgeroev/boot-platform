import {
  HttpCallerFactory,
  HttpRequestMethod,
} from "../../../../hook/useHttpHook";
import { ArticleLikeActionModel } from "../../model/ArticleLikeAction";
import { MathsRoadMapBaseRequest } from "./MathsRoadMapBaseRequest";

export interface SubmitArticleDislikeRequestVariables {
  id: number;
}

export class SubmitArticleDislikeRequest extends MathsRoadMapBaseRequest<
  ArticleLikeActionModel,
  any,
  SubmitArticleDislikeRequestVariables
> {
  constructor(httpCallerFactory: HttpCallerFactory) {
    super(ArticleLikeActionModel, httpCallerFactory);
  }

  public isAuthorized(): boolean {
    return true;
  }

  public getHttpRequestMethod(): HttpRequestMethod {
    return "patch";
  }

  public getRelativeApiPath(
    requstVariables?: SubmitArticleDislikeRequestVariables,
  ): string {
    return `article/${requstVariables?.id}/dislike`;
  }

  public static build(
    httpCallerFactory: HttpCallerFactory,
  ): SubmitArticleDislikeRequest {
    return new SubmitArticleDislikeRequest(httpCallerFactory);
  }
}
