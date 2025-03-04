import { BaseModel } from "../../../model/BaseModel";
import { AccessOp } from "./AccessOp";
import { UserModel } from "./UserModel";

export class UserWithPrivilegesModel extends BaseModel {
  public get user(): UserModel {
    return this.getModel<UserModel>(UserModel, "user");
  }

  public get privileges(): Map<string, AccessOp[]> {
    let raw = this.data["privileges"];
    if (raw instanceof Map) {
      return raw;
    }
    const map = new Map<string, AccessOp[]>(
      Object.entries(raw).map(([key, value]) => [key, value as AccessOp[]]),
    );
    return map;
  }

  public static parse(data: any): UserWithPrivilegesModel {
    return new UserWithPrivilegesModel(data);
  }
}
