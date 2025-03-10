import { BaseModel } from "../../../model/BaseModel";

export class ArticleModel extends BaseModel {
  public get title(): string {
    return this.getString("title");
  }
  public get description(): string | null {
    return this.getStringNullable("description") || null;
  }
  public get id(): number {
    return this.getNumber("id");
  }

  public get creatorId(): number {
    return this.getNumber("creatorId");
  }

  public get createdAt(): Date {
    return this.getDate("createdAt");
  }

  public get updatedAt(): Date {
    return this.getDate("updatedAt");
  }

  public static parse(data: any): ArticleModel {
    return new ArticleModel(data);
  }
}
