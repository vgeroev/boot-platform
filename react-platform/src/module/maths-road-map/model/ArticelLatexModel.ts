import { BaseModel } from "../../../model/BaseModel";

export class ArticleLatexModel extends BaseModel {
  public get latex(): string {
    return this.getString("latex");
  }
  public get configuration(): string | null {
    return this.getStringNullable("configuration");
  }
  public get id(): number {
    return this.getNumber("id");
  }

  public static parse(data: any): ArticleLatexModel {
    return new ArticleLatexModel(data);
  }
}
