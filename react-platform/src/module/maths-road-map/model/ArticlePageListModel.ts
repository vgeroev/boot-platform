import { BaseModel } from "../../../model/BaseModel";
import { TagModel } from "../../core/model/TagModel";
import { ArticleListModel } from "./ArticleListModel";

export class ArticlePageListModel extends BaseModel {
  public get tags(): Array<TagModel> {
    return this.getModelArray(TagModel, "tags");
  }

  public get articles(): ArticleListModel {
    return this.getModel(ArticleListModel, "articles");
  }
  public static parse(data: any): ArticlePageListModel {
    return new ArticlePageListModel(data);
  }
}
