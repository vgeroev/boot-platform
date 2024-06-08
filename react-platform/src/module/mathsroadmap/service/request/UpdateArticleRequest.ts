import {
  HttpCallerFactory,
  HttpRequestMethod,
} from "../../../../hook/useHttpHook";
import { ArticleModel } from "../../model/ArticleModel";
import { UpdateArticleModel } from "../../model/UpdateArticleModel";
import { MathsRoadMapBaseRequest } from "./MathsRoadMapBaseRequest";

export interface UpdateArticleRequestVariables {
  id: number;
}

export interface UpdateArticleRequestData {
  title?: string;
  description?: string | null;
}

export class UpdateArticleRequest extends MathsRoadMapBaseRequest<
  UpdateArticleModel,
  UpdateArticleRequestData,
  UpdateArticleRequestVariables
> {
  constructor(httpCallerFactory: HttpCallerFactory) {
    super(UpdateArticleModel, httpCallerFactory, true);
  }

  public getHttpRequestMethod(): HttpRequestMethod {
    return "patch";
  }

  public getRelativeApiPath(
    requstVariables?: UpdateArticleRequestVariables,
  ): string {
    return `article/${requstVariables?.id}`;
  }

  public static build(
    httpCallerFactory: HttpCallerFactory,
  ): UpdateArticleRequest {
    return new UpdateArticleRequest(httpCallerFactory);
  }
}
