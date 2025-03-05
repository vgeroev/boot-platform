import {
  HttpCallerFactory,
  HttpRequestMethod,
} from "../../../hook/useHttpHook";
import { TagModel } from "../model/TagModel";
import { CoreBaseRequest } from "./CoreBaseRequest";

export interface CreateTagForm {
  name: string;
  hexColor: string;
}

export class CreateTagRequest extends CoreBaseRequest<TagModel> {
  constructor(httpCallerFactory: HttpCallerFactory) {
    super(TagModel, httpCallerFactory);
  }

  public isAuthorized(): boolean {
    return true;
  }

  public getHttpRequestMethod(): HttpRequestMethod {
    return "post";
  }
  public getRelativeApiPath(): string {
    return "tag/create";
  }

  public static build(httpCallerFactory: HttpCallerFactory): CreateTagRequest {
    return new CreateTagRequest(httpCallerFactory);
  }
}
