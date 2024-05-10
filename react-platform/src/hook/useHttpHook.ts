import axios from "axios";
import { AuthState, useAuth } from "react-oidc-context";
import { IModelParser, ModelFactory } from "../model/BaseModel";

const httpCall = <M = {}, D = any>(
  authorized: boolean,
  authState?: AuthState,
): HttpCaller<M, D> => {
  return {
    isAuthorized: authorized,
    exec: async ({
      request,
      data,
      model,
    }: HttpRequestHookProps<D>): Promise<HttpResponse<M | ModuleError>> => {
      let headers: any;
      if (authorized) {
        const token = authState?.user?.access_token;
        if (!token) {
          throw new Error("Failed to get token");
        }
        headers = { Authorization: `Bearer ${token}` };
      } else {
        headers = undefined;
      }

      return await axios
        .request({
          url: request.url,
          method: request.method,
          headers: headers,
          data: data,
          validateStatus: () => true,
        })
        .catch((e) => {
          console.log(e);
          throw new Error(e);
        })
        .then((rawResponse: any) => {
          const data: any = rawResponse.data;
          if (!data) {
            console.log("No data in " + rawResponse);
            throw new Error("No data in " + rawResponse);
          }

          const moduleError: Record<string, unknown> = data.moduleError;
          if (moduleError) {
            if (typeof moduleError.code !== "string") {
              throw new Error("No `code` field " + moduleError);
            }

            return {
              httpStatus: rawResponse.status,
              response: new ModuleError(
                moduleError.code,
                moduleError?.parameters as Record<string, unknown> | undefined,
                moduleError?.message as string | undefined,
              ),
            };
          }
          if (model) {
            return {
              httpStatus: rawResponse.status,
              response: new ModelFactory(model).getModel(data),
            };
          }
          return { httpStatus: rawResponse.status } as HttpResponse<M>;
        });
    },
  };
};

export type HttpRequestMethod = "get" | "post" | "delete" | "patch" | "put";

export interface HttpRequest {
  method: HttpRequestMethod;
  url: string;
}
export interface HttpRequestHookProps<D = any> {
  request: HttpRequest;
  data?: D;
  model?: IModelParser;
}

export interface HttpCaller<M = {}, D = any> {
  exec: (x: HttpRequestHookProps<D>) => Promise<HttpResponse<M | ModuleError>>;
  isAuthorized: boolean;
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
  const auth: AuthState = useAuth();
  return {
    newInstance: <M = {}, D = any>(authorized: boolean): HttpCaller<M, D> => {
      if (authorized) {
        return httpCall<M>(true, auth);
      } else {
        return httpCall<M>(false);
      }
    },
  };
};
