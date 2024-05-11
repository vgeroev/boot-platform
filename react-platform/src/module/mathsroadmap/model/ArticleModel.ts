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
  public get abstractionLevel(): AbstractionLevel {
    return AbstractionLevel[
      this.getString("abstractionLevel") as keyof typeof AbstractionLevel
    ];
  }
  public get id(): number {
    return this.getNumber("id");
  }

  public static parse(data: any): ArticleModel {
    return new ArticleModel(data);
  }
}
