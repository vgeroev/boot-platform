import { Modal } from "antd";
import {
  HttpCaller,
  HttpCallerFactory,
  HttpRequest,
  HttpRequestHookProps,
  HttpRequestMethod,
  ModuleError,
} from "../../hook/useHttpHook";
import { BaseModel, IModelParser, ModelFactory } from "../../model/BaseModel";
import { getLoginRoute } from "../../module/security/route/SecurityRoutes";

export interface RequestResult<T extends BaseModel | ModuleError | any> {
  status: number;
  data: T;
}

export interface ExecOptions<M extends BaseModel, D = any, R = any, P = any> {
  data?: D;
  requestVariables?: R;
  requestParams?: P;
  onSuccess?: (httpResponse: RequestResult<M>) => void;
  handleModuleError?: (httpResponse: RequestResult<ModuleError>) => boolean;
  onFinally?: () => void;
  onCompletion?: (httpResponse: RequestResult<any>) => void;
}

export abstract class BaseRequest<M extends BaseModel, D = any, R = any> {
  private readonly httpCaller: HttpCaller<M, D>;

  constructor(
    private readonly modelParser: IModelParser,
    httpCallerFactory: HttpCallerFactory,
  ) {
    this.httpCaller = httpCallerFactory.newInstance<M, D>();
  }

  public exec({
    data,
    requestVariables,
    requestParams,
    onSuccess,
    handleModuleError,
    onFinally,
    onCompletion,
  }: ExecOptions<M, D>): void {
    let props: HttpRequestHookProps = {
      request: {
        ...this.constructHttpRequest(requestVariables),
        params: requestParams,
      },
      model: this.modelParser,
    };
    if (data) {
      props = { ...props, data: data };
    }

    const promise: Promise<void> = this.httpCaller
      .exec(props)
      .then((httpResponse) => {
        const response: ModuleError | M | undefined = httpResponse.data;
        if (onCompletion) {
          onCompletion({ status: httpResponse.httpStatus, data: response });
          return;
        }

        if (
          httpResponse.httpStatus === 401 ||
          httpResponse.httpStatus === 403
        ) {
          window.location.href = getLoginRoute();
          return;
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
          if (response) {
            onSuccess({ status: httpResponse.httpStatus, data: response });
          } else {
            let empty: M = new ModelFactory(this.modelParser).getModel({});
            onSuccess({ status: httpResponse.httpStatus, data: empty });
          }
        }
      });

    if (onFinally) {
      promise.finally(onFinally);
    }
  }

  public abstract isAuthorized(): boolean;

  public abstract getApiVersion(): string;

  public abstract getModule(): string;

  public abstract getHttpRequestMethod(): HttpRequestMethod;

  public abstract getRelativeApiPath(requestVariables?: R): string;

  public constructHttpRequest(requestVariables?: R): HttpRequest {
    const authPath: string = this.isAuthorized() ? "authorized" : "anon";
    return {
      method: this.getHttpRequestMethod(),
      url: `/${authPath}/${this.getModule()}/${this.getApiVersion()}/${this.getRelativeApiPath(requestVariables)}`,
    };
  }
}

export interface IRequestBuilder {
  new (...args: any[]): any;
  build(httpCallerFactory: HttpCallerFactory): InstanceType<this>;
}

export class RequestFactory<
  M extends BaseModel,
  I extends IRequestBuilder,
  R extends BaseRequest<M> = InstanceType<I>,
> {
  constructor(private readonly request: I) {}

  public build(httpCallerFactory: HttpCallerFactory): R {
    return this.request.build(httpCallerFactory);
  }
}
