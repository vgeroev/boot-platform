import { BaseModel } from "./BaseModel";

export class InitializedModulesModel extends BaseModel {
  public get moduleUUIDs(): string[] {
    return this.getStringArray();
  }

  public static parse(data: any): InitializedModulesModel {
    return new InitializedModulesModel(data);
  }
}
