import {
  HttpCallerFactory,
  HttpRequestMethod,
} from "../../../../hook/useHttpHook";
import { AbstractionLevel } from "../../model/ArticleModel";
import { CreateArticleModel } from "../../model/CreateArticleModel";
import { MathsRoadMapBaseRequest } from "./MathsRoadMapBaseRequest";

export interface CreateArticleRequestData {
  title: string;
  abstractionLevel: AbstractionLevel;
  latex: string;
  configuration: string | undefined;
}

export class CreateArticleRequest extends MathsRoadMapBaseRequest<
  CreateArticleModel,
  CreateArticleRequestData
> {
  constructor(httpCallerFactory: HttpCallerFactory) {
    super(CreateArticleModel, httpCallerFactory, true);
  }

  public getHttpRequestMethod(): HttpRequestMethod {
    return "post";
  }
  public getRelativeApiPath(): string {
    return "article";
  }

  public static build(
    httpCallerFactory: HttpCallerFactory,
  ): CreateArticleRequest {
    return new CreateArticleRequest(httpCallerFactory);
  }
}
