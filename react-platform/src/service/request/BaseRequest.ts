import {
  HttpCaller,
  HttpCallerFactory,
  HttpRequest,
  HttpRequestHookProps,
  HttpRequestMethod,
  HttpResponse,
  ModuleError,
} from "../../hook/useHttpHook";
import { BaseModel, IModelParser } from "../../model/BaseModel";

export interface ExecOptions<M extends BaseModel, D = any> {
  data?: D;
  onSuccess?: (httpResponse: HttpResponse<M>) => void;
  onModuleError?: (httpResponse: HttpResponse<ModuleError>) => void;
  onFinally?: () => void;
}

export abstract class BaseRequest<M extends BaseModel | {} = {}, D = any> {
  private readonly httpCaller: HttpCaller<M, D>;

  constructor(
    private readonly modelParser: IModelParser,
    httpCallerFactory: HttpCallerFactory,
    authorized: boolean,
  ) {
    this.httpCaller = httpCallerFactory.newInstance<M, D>(authorized);
  }

  public exec({
    data,
    onSuccess,
    onModuleError,
    onFinally,
  }: ExecOptions<M, D>): void {
    let props: HttpRequestHookProps = { request: this.getHttpRequest() };
    if (data) {
      props = { ...props, data: data };
    }
    if (this.modelParser) {
      props = { ...props, model: this.modelParser };
    }

    const promise: Promise<void> = this.httpCaller
      .exec(props)
      .then((httpResponse) => {
        const response: ModuleError | M | undefined = httpResponse.data;
        if (!response) {
          return;
        }

        if (onModuleError && response instanceof ModuleError) {
          onModuleError(httpResponse as HttpResponse<ModuleError>);
        } else if (onSuccess) {
          onSuccess(httpResponse as HttpResponse<M>);
        }
      });

    if (onFinally) {
      promise.finally(onFinally);
    }
  }

  public isAuthorized(): boolean {
    return this.httpCaller.isAuthorized;
  }

  public abstract getApiVersion(): string;

  public abstract getModule(): string;

  public abstract getHttpRequestMethod(): HttpRequestMethod;

  public abstract getRelativeApiPath(): string;

  private getHttpRequest(): HttpRequest {
    const authPath: string = this.isAuthorized() ? "authorized" : "anon";
    return {
      method: this.getHttpRequestMethod(),
      url: `/${authPath}/${this.getModule()}/${this.getApiVersion()}/${this.getRelativeApiPath()}`,
    };
  }
}

export interface IRequestBuilder {
  new (...args: any[]): any;
  build(httpCallerFactory: HttpCallerFactory): InstanceType<this>;
}

export class RequestFactory<
  I extends IRequestBuilder,
  R extends BaseRequest = InstanceType<I>,
> {
  constructor(private readonly request: I) {}

  public build(httpCallerFactory: HttpCallerFactory): R {
    return this.request.build(httpCallerFactory);
  }
}
