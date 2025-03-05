import {
  HttpCallerFactory,
  HttpRequestMethod,
} from "../../../../hook/useHttpHook";
import { ArticleTagsModel } from "../../model/ArticleTagsModel";
import { MathsRoadMapBaseRequest } from "./MathsRoadMapBaseRequest";

export interface GetTagsRequestVariables {
  articleId: number;
}

export class GetTagsByArticleRequest extends MathsRoadMapBaseRequest<ArticleTagsModel> {
  constructor(httpCallerFactory: HttpCallerFactory) {
    super(ArticleTagsModel, httpCallerFactory);
  }

  public isAuthorized(): boolean {
    return true;
  }

  public getHttpRequestMethod(): HttpRequestMethod {
    return "get";
  }

  public getRelativeApiPath(requstVariables?: GetTagsRequestVariables): string {
    return `article/${requstVariables?.articleId}/tags`;
  }

  public static build(
    httpCallerFactory: HttpCallerFactory,
  ): GetTagsByArticleRequest {
    return new GetTagsByArticleRequest(httpCallerFactory);
  }
}
