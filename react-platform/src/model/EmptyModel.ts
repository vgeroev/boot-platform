import { BaseModel } from "./BaseModel";

export class EmptyModel extends BaseModel {
  public static parse(data: any): EmptyModel {
    return new EmptyModel({});
  }
}
