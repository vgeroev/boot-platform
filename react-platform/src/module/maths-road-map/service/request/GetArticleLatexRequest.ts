import {
  HttpCallerFactory,
  HttpRequestMethod,
} from "../../../../hook/useHttpHook";
import { ArticleLatexModel } from "../../model/ArticelLatexModel";
import { MathsRoadMapBaseRequest } from "./MathsRoadMapBaseRequest";

export interface GetArticleLatexRequestVariables {
  id: number;
}

export class GetArticleLatexRequest extends MathsRoadMapBaseRequest<
  ArticleLatexModel,
  any,
  GetArticleLatexRequestVariables
> {
  constructor(httpCallerFactory: HttpCallerFactory) {
    super(ArticleLatexModel, httpCallerFactory, true);
  }

  public getHttpRequestMethod(): HttpRequestMethod {
    return "get";
  }

  public getRelativeApiPath(
    requstVariables?: GetArticleLatexRequestVariables,
  ): string {
    return `article-latex/${requstVariables?.id}`;
  }

  public static build(
    httpCallerFactory: HttpCallerFactory,
  ): GetArticleLatexRequest {
    return new GetArticleLatexRequest(httpCallerFactory);
  }
}
