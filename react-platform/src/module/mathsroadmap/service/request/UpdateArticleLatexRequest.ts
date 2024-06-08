import {
  HttpCallerFactory,
  HttpRequestMethod,
} from "../../../../hook/useHttpHook";
import { UpdateArticleLatexModel } from "../../model/UpdateArticleLatexModel";
import { MathsRoadMapBaseRequest } from "./MathsRoadMapBaseRequest";

export interface UpdateArticleLatexRequestVariables {
  id: number;
}

export interface UpdateArticleLatexRequestData {
  title?: string;
  description?: string | null;
}

export class UpdateArticleLatexRequest extends MathsRoadMapBaseRequest<
  UpdateArticleLatexModel,
  UpdateArticleLatexRequestData,
  UpdateArticleLatexRequestVariables
> {
  constructor(httpCallerFactory: HttpCallerFactory) {
    super(UpdateArticleLatexModel, httpCallerFactory, true);
  }

  public getHttpRequestMethod(): HttpRequestMethod {
    return "patch";
  }

  public getRelativeApiPath(
    requstVariables?: UpdateArticleLatexRequestVariables,
  ): string {
    return `article-latex/${requstVariables?.id}`;
  }

  public static build(
    httpCallerFactory: HttpCallerFactory,
  ): UpdateArticleLatexRequest {
    return new UpdateArticleLatexRequest(httpCallerFactory);
  }
}
