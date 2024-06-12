import { BaseModel } from "../../../model/BaseModel";
import { ArticleModel } from "./ArticleModel";

export class UpdateArticleLatexModel extends BaseModel {
  public get article(): ArticleModel {
    return this.getModel(ArticleModel, "article");
  }
  public get url(): string {
    return this.getString("url");
  }

  public static parse(data: any): UpdateArticleLatexModel {
    return new UpdateArticleLatexModel(data);
  }
}
