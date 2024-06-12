import { IModelParser } from "../../../model/BaseModel";
import { PaginatedModel } from "../../../model/PaginatedModel";
import { ArticleModel } from "./ArticleModel";

export class ArticleListModel extends PaginatedModel<ArticleModel> {
  public getPaginatedModel(): IModelParser {
    return ArticleListModel;
  }

  public static parse(data: any): ArticleListModel {
    return new ArticleListModel(data);
  }
}
