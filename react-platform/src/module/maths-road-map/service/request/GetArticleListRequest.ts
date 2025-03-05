import {
  HttpCallerFactory,
  HttpRequestMethod,
} from "../../../../hook/useHttpHook";
import { ArticlePageListModel } from "../../model/ArticlePageListModel";
import { MathsRoadMapBaseRequest } from "./MathsRoadMapBaseRequest";

export class GetArticleListRequest extends MathsRoadMapBaseRequest<ArticlePageListModel> {
  constructor(httpCallerFactory: HttpCallerFactory) {
    super(ArticlePageListModel, httpCallerFactory);
  }

  public isAuthorized(): boolean {
    return false;
  }

  public getHttpRequestMethod(): HttpRequestMethod {
    return "get";
  }

  public getRelativeApiPath(): string {
    return `article/list`;
  }

  public static build(
    httpCallerFactory: HttpCallerFactory,
  ): GetArticleListRequest {
    return new GetArticleListRequest(httpCallerFactory);
  }
}
