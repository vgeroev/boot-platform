import {
  HttpCallerFactory,
  HttpRequestMethod,
} from "../../../../hook/useHttpHook";
import { EmptyModel } from "../../../../model/EmptyModel";
import { MathsRoadMapBaseRequest } from "./MathsRoadMapBaseRequest";

export interface AddTagToArticleRequestVariables {
  articleId: number;
  tagId: number;
}

export class AddTagToArticleRequest extends MathsRoadMapBaseRequest<
  EmptyModel,
  any,
  AddTagToArticleRequestVariables
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
    requstVariables?: AddTagToArticleRequestVariables,
  ): string {
    return `article/${requstVariables?.articleId}/add-tag/${requstVariables?.tagId}`;
  }

  public static build(
    httpCallerFactory: HttpCallerFactory,
  ): AddTagToArticleRequest {
    return new AddTagToArticleRequest(httpCallerFactory);
  }
}
