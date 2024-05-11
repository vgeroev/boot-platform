import { Modal } from "antd";
import { AuthContextProps } from "react-oidc-context";
import {
  HttpCaller,
  HttpCallerFactory,
  HttpRequest,
  HttpRequestHookProps,
  HttpRequestMethod,
  ModuleError,
} from "../../hook/useHttpHook";
import { BaseModel, IModelParser } from "../../model/BaseModel";

export interface RequestResult<T extends BaseModel | ModuleError> {
  status: number;
  data: T;
}

export interface ExecOptions<M extends BaseModel, D = any> {
  data?: D;
  onSuccess?: (httpResponse: RequestResult<M>) => void;
  handleModuleError?: (httpResponse: RequestResult<ModuleError>) => boolean;
  onFinally?: () => void;
}

export abstract class BaseRequest<M extends BaseModel, D = any> {
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
    handleModuleError,
    onFinally,
  }: ExecOptions<M, D>): void {
    let props: HttpRequestHookProps = {
      request: this.getHttpRequest(),
      model: this.modelParser,
    };
    if (data) {
      props = { ...props, data: data };
    }

    const promise: Promise<void> = this.httpCaller
      .exec(props)
      .then((httpResponse) => {
        if (httpResponse.httpStatus === 401) {
          const auth: AuthContextProps = this.httpCaller.authState;
          auth.signinRedirect();
        }

        const response: ModuleError | M | undefined = httpResponse.data;
        if (!response) {
          throw new Error("No response in " + httpResponse);
        }

        if (response instanceof ModuleError) {
          let errorHandled: boolean = false;
          if (handleModuleError) {
            errorHandled = handleModuleError({
              status: httpResponse.httpStatus,
              data: response,
            });
          }

          if (!errorHandled) {
            Modal.error({ title: response.code, content: response.message });
          }
        } else if (onSuccess) {
          onSuccess({ status: httpResponse.httpStatus, data: response });
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
  new(...args: any[]): any;
  build(httpCallerFactory: HttpCallerFactory): InstanceType<this>;
}

export class RequestFactory<
  M extends BaseModel,
  I extends IRequestBuilder,
  R extends BaseRequest<M> = InstanceType<I>,
> {
  constructor(private readonly request: I) { }

  public build(httpCallerFactory: HttpCallerFactory): R {
    return this.request.build(httpCallerFactory);
  }
}
