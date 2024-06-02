import { Modal } from "antd";
import axios, { AxiosResponse } from "axios";
import { AuthContextProps, useAuth } from "react-oidc-context";
import { IModelParser, ModelFactory } from "../model/BaseModel";

type HttpResult = Record<"moduleError" | "data", any>;

const httpCall = <M, D = any>(
  authorized: boolean,
  authState: AuthContextProps,
): HttpCaller<M, D> => {
  return {
    isAuthorized: authorized,
    authState: authState,
    exec: async ({
      request,
      data,
      model,
    }: HttpRequestHookProps<D>): Promise<HttpResponse<M | ModuleError>> => {
      let headers: any;
      if (authorized) {
        const token = authState?.user?.access_token;
        if (!token) {
          authState.signinRedirect();
        }
        headers = { Authorization: `Bearer ${token}` };
      } else {
        headers = undefined;
      }

      return await axios
        .request<HttpResult>({
          url: request.url,
          method: request.method,
          params: request.params,
          headers: headers,
          data: data,
          validateStatus: (status) => status < 500,
        })
        .catch((e) => {
          console.log(e);
          Modal.error({
            title: "Server error",
          });
          throw new Error(e);
        })
        .then((axiosResponse: AxiosResponse<HttpResult>) => {
          const data: HttpResult = axiosResponse.data;
          if (!data) {
            return { httpStatus: axiosResponse.status };
          }

          if (data.data) {
            return {
              httpStatus: axiosResponse.status,
              data: new ModelFactory(model).getModel(data.data),
            };
          } else if (data.moduleError) {
            const moduleError: any = data.moduleError;
            if (typeof moduleError.code !== "string") {
              throw new Error(
                "No `code` field with `string` type in" + moduleError,
              );
            }

            return {
              httpStatus: axiosResponse.status,
              data: new ModuleError(
                moduleError.code,
                moduleError?.parameters as Record<string, unknown> | undefined,
                moduleError?.message as string | undefined,
              ),
            };
          }

          throw new Error("Unknown response " + axiosResponse);
        });
    },
  };
};

export type HttpRequestMethod = "get" | "post" | "delete" | "patch" | "put";

export interface HttpRequest {
  method: HttpRequestMethod;
  url: string;
  params?: any;
}
export interface HttpRequestHookProps<D = any> {
  request: HttpRequest;
  model: IModelParser;
  data?: D;
}

export interface HttpCaller<M = {}, D = any> {
  exec: (x: HttpRequestHookProps<D>) => Promise<HttpResponse<M | ModuleError>>;
  isAuthorized: boolean;
  authState: AuthContextProps;
}

export interface HttpCallerFactory {
  newInstance: <M, D = any>(authorized: boolean) => HttpCaller<M, D>;
}

export class ModuleError {
  constructor(
    private readonly _code: string,
    private readonly _parameters?: Record<string, unknown>,
    private readonly _message?: string,
  ) {}
  public get message(): string | undefined {
    return this._message;
  }
  public get parameters(): Record<string, unknown> | undefined {
    return this._parameters;
  }
  public get code(): string {
    return this._code;
  }
}

export interface HttpResponse<R> {
  httpStatus: number;
  data?: R;
}

export const useHttpCallerFactory = (): HttpCallerFactory => {
  const auth: AuthContextProps = useAuth();
  return {
    newInstance: <M = {}, D = any>(authorized: boolean): HttpCaller<M, D> => {
      if (authorized) {
        return httpCall<M>(true, auth);
      } else {
        return httpCall<M>(false, auth);
      }
    },
  };
};
