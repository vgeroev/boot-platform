import {
  HttpCallerFactory,
  HttpRequestMethod,
} from "../../../hook/useHttpHook";
import { TagListModel } from "../model/TagListModel";
import { CoreBaseRequest } from "./CoreBaseRequest";

export class GetTagListRequest extends CoreBaseRequest<TagListModel> {
  constructor(httpCallerFactory: HttpCallerFactory) {
    super(TagListModel, httpCallerFactory);
  }

  public isAuthorized(): boolean {
    return false;
  }

  public getHttpRequestMethod(): HttpRequestMethod {
    return "get";
  }
  public getRelativeApiPath(): string {
    return "tag/list";
  }

  public static build(httpCallerFactory: HttpCallerFactory): GetTagListRequest {
    return new GetTagListRequest(httpCallerFactory);
  }
}
