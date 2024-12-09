import {
  HttpCallerFactory,
  HttpRequestMethod,
} from "../../../../hook/useHttpHook";
import { CreateArticleModel } from "../../model/CreateArticleModel";
import { MathsRoadMapBaseRequest } from "./MathsRoadMapBaseRequest";

export interface CreateArticleRequestData {
  title: string;
  description: string | null;
  latex: string;
  configuration: string | undefined;
}

export class CreateArticleRequest extends MathsRoadMapBaseRequest<
  CreateArticleModel,
  CreateArticleRequestData
> {
  constructor(httpCallerFactory: HttpCallerFactory) {
    super(CreateArticleModel, httpCallerFactory);
  }

  public isAuthorized(): boolean {
    return true;
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
