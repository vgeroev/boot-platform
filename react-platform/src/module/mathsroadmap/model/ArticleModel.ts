import { BaseModel } from "../../../model/BaseModel";

export enum AbstractionLevel {
  SUPREME = "SUPREME",
  MEDIUM = "MEDIUM",
  LOW = "LOW",
}

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

  public get creatorUsername(): string {
    return this.getString("creatorUsername");
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
