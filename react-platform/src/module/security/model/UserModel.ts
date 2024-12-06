import { BaseModel } from "../../../model/BaseModel";

export class UserModel extends BaseModel {
  public get id(): number {
    return this.getNumber("id");
  }

  public get username(): string {
    return this.getString("username");
  }

  public get createdAt(): Date {
    return this.getDate("createdAt");
  }

  public get updatedAt(): Date {
    return this.getDate("updatedAt");
  }

  public static parse(data: any): UserModel {
    return new UserModel(data);
  }
}
