import { BaseModel } from "../../../model/BaseModel";

export class PreviewArticleModel extends BaseModel {
  public get articleURL(): string {
    return this.getString("articleURL");
  }

  public static parse(data: any): PreviewArticleModel {
    return new PreviewArticleModel(data);
  }
}
