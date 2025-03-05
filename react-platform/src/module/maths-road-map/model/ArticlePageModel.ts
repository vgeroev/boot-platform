import { BaseModel } from "../../../model/BaseModel";
import { UserModel } from "../../security/model/UserModel";

export class ArticlePageModel extends BaseModel {
  public get title(): string {
    return this.getString("title");
  }
  public get description(): string | null {
    return this.getStringNullable("description") || null;
  }
  public get id(): number {
    return this.getNumber("id");
  }

  public get creator(): UserModel {
    return this.getModel<UserModel>(UserModel, "creator");
  }

  public get createdAt(): Date {
    return this.getDate("createdAt");
  }

  public get updatedAt(): Date {
    return this.getDate("updatedAt");
  }

  public get likes(): number {
    return this.getNumber("likes");
  }

  public get dislikes(): number {
    return this.getNumber("dislikes");
  }

  public get tagIds(): Array<number> {
    return this.getNumberArray("tagIds") || [];
  }

  public static parse(data: any): ArticlePageModel {
    return new ArticlePageModel(data);
  }
}
