import { BaseModel } from "../../../model/BaseModel";

export class TagModel extends BaseModel {
  public get name(): string {
    return this.getString("name");
  }
  public get hexColor(): string {
    return this.getString("hexColor");
  }
  public get id(): number {
    return this.getNumber("id");
  }

  public static parse(data: any): TagModel {
    return new TagModel(data);
  }
}
