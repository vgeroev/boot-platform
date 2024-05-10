import { BaseModel } from "../model/BaseModel";
import { BaseRequest, IRequestBuilder } from "../service/request/BaseRequest";
import { HttpCallerFactory, useHttpCallerFactory } from "./useHttpHook";

export const useHttpRequest = <M extends BaseModel | {} = {}, D = any>(
  factory: IRequestBuilder,
): BaseRequest<M, D> => {
  const httpCallerFactory: HttpCallerFactory = useHttpCallerFactory();
  return factory.build(httpCallerFactory);
};
