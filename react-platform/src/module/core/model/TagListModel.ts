import { IModelParser } from "../../../model/BaseModel";
import { PaginatedModel } from "../../../model/PaginatedModel";
import { TagModel } from "./TagModel";

export class TagListModel extends PaginatedModel<TagModel> {
  public getPaginatedModel(): IModelParser {
    return TagListModel;
  }

  public static parse(data: any): TagListModel {
    return new TagListModel(data);
  }
}
