import {
  HttpCallerFactory,
  HttpRequestMethod,
} from "../../../../hook/useHttpHook";
import { EmptyModel } from "../../../../model/EmptyModel";
import { MathsRoadMapBaseRequest } from "./MathsRoadMapBaseRequest";

export interface RemoveTagFromArticleRequestVariables {
  articleId: number;
  tagId: number;
}

export class RemoveTagFromArticleRequest extends MathsRoadMapBaseRequest<
  EmptyModel,
  any,
  RemoveTagFromArticleRequestVariables
> {
  constructor(httpCallerFactory: HttpCallerFactory) {
    super(EmptyModel, httpCallerFactory);
  }

  public isAuthorized(): boolean {
    return true;
  }

  public getHttpRequestMethod(): HttpRequestMethod {
    return "patch";
  }

  public getRelativeApiPath(
    requstVariables?: RemoveTagFromArticleRequestVariables,
  ): string {
    return `article/${requstVariables?.articleId}/remove-tag/${requstVariables?.tagId}`;
  }

  public static build(
    httpCallerFactory: HttpCallerFactory,
  ): RemoveTagFromArticleRequest {
    return new RemoveTagFromArticleRequest(httpCallerFactory);
  }
}
