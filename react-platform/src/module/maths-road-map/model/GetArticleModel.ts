import { BaseModel } from "../../../model/BaseModel";
import { ArticlePageModel } from "./ArticlePageModel";

export class GetArticleModel extends BaseModel {
  public get article(): ArticlePageModel {
    return this.getModel(ArticlePageModel, "article");
  }
  public get url(): string {
    return this.getString("url");
  }

  public static parse(data: any): GetArticleModel {
    return new GetArticleModel(data);
  }
}
