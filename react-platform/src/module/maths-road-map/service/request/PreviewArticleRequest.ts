import {
  HttpCallerFactory,
  HttpRequestMethod,
} from "../../../../hook/useHttpHook";
import { PreviewArticleModel } from "../../model/PreviewArticleModel";
import { MathsRoadMapBaseRequest } from "./MathsRoadMapBaseRequest";

export interface PreviewArticleRequestData {
  latex: string;
  configuration: string | undefined;
}

export class PreviewArticleRequest extends MathsRoadMapBaseRequest<
  PreviewArticleModel,
  PreviewArticleRequestData
> {
  constructor(httpCallerFactory: HttpCallerFactory) {
    super(PreviewArticleModel, httpCallerFactory);
  }

  public isAuthorized(): boolean {
    return true;
  }

  public getHttpRequestMethod(): HttpRequestMethod {
    return "post";
  }
  public getRelativeApiPath(): string {
    return "article/tex4ht/preview";
  }

  public static build(
    httpCallerFactory: HttpCallerFactory,
  ): PreviewArticleRequest {
    return new PreviewArticleRequest(httpCallerFactory);
  }
}
