import { IModelParser } from "../../../model/BaseModel";
import { PaginatedModel } from "../../../model/PaginatedModel";
import { ArticleWithCreatorModel } from "./ArticleWithCreatorModel";

export class ArticleListModel extends PaginatedModel<ArticleWithCreatorModel> {
  public getPaginatedModel(): IModelParser {
    return ArticleListModel;
  }

  public static parse(data: any): ArticleListModel {
    return new ArticleListModel(data);
  }
}
