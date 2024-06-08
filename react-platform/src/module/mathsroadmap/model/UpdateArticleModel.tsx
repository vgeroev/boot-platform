import { BaseModel } from "../../../model/BaseModel";
import { ArticleModel } from "./ArticleModel";

export class UpdateArticleModel extends BaseModel {
  public get article(): ArticleModel {
    return this.getModel(ArticleModel, "article");
  }
  public get url(): string {
    return this.getString("url");
  }

  public static parse(data: any): UpdateArticleModel {
    return new UpdateArticleModel(data);
  }
}
