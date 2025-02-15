import {
  HttpCallerFactory,
  HttpRequestMethod,
} from "../../../../hook/useHttpHook";
import { ArticleLikeActionModel } from "../../model/ArticleLikeAction";
import { MathsRoadMapBaseRequest } from "./MathsRoadMapBaseRequest";

export interface SubmitArticleLikeRequestVariables {
  id: number;
}

export class SubmitArticleLikeRequest extends MathsRoadMapBaseRequest<
  ArticleLikeActionModel,
  any,
  SubmitArticleLikeRequestVariables
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
    requstVariables?: SubmitArticleLikeRequestVariables,
  ): string {
    return `article/${requstVariables?.id}/like`;
  }

  public static build(
    httpCallerFactory: HttpCallerFactory,
  ): SubmitArticleLikeRequest {
    return new SubmitArticleLikeRequest(httpCallerFactory);
  }
}
