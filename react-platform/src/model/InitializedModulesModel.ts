import { BaseModel } from "./BaseModel";

export class InitializedModulesModel implements BaseModel {
  public get moduleUUIDs(): string[] {
    return this._moduleUUIDs;
  }
  constructor(private readonly _moduleUUIDs: string[]) {}

  public static parse(data: Record<string, unknown>): InitializedModulesModel {
    const moduleUUIDs: string[] | unknown = data;
    if (!moduleUUIDs || !(moduleUUIDs instanceof Array<string>)) {
      throw new Error("No moduleUUIDs " + data);
    }

    const model: InitializedModulesModel = new InitializedModulesModel(
      moduleUUIDs,
    );
    return model;
  }
}
