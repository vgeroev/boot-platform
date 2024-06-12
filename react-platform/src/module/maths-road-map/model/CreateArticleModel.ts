import { BaseModel } from "../../../model/BaseModel";
import { ArticleModel } from "./ArticleModel";

export class CreateArticleModel extends BaseModel {
  public get article(): ArticleModel {
    return this.getModel(ArticleModel, "article");
  }
  public get url(): string {
    return this.getString("url");
  }

  public static parse(data: any): CreateArticleModel {
    return new CreateArticleModel(data);
  }
}
