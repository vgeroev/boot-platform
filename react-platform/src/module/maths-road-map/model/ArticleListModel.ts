import { IModelParser } from "../../../model/BaseModel";
import { PaginatedModel } from "../../../model/PaginatedModel";
import { ArticlePageModel } from "./ArticlePageModel";

export class ArticleListModel extends PaginatedModel<ArticlePageModel> {
  public getPaginatedModel(): IModelParser {
    return ArticleListModel;
  }

  public static parse(data: any): ArticleListModel {
    return new ArticleListModel(data);
  }
}
