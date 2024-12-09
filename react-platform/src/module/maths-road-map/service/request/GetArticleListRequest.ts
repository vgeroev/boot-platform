import {
  HttpCallerFactory,
  HttpRequestMethod,
} from "../../../../hook/useHttpHook";
import { ArticleListModel } from "../../model/ArticleListModel";
import { MathsRoadMapBaseRequest } from "./MathsRoadMapBaseRequest";

export class GetArticleListRequest extends MathsRoadMapBaseRequest<ArticleListModel> {
  constructor(httpCallerFactory: HttpCallerFactory) {
    super(ArticleListModel, httpCallerFactory);
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
