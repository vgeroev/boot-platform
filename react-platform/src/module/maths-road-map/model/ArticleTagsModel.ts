import { BaseModel } from "../../../model/BaseModel";
import { TagModel } from "../../core/model/TagModel";

export class ArticleTagsModel extends BaseModel {
  public get result(): Array<TagModel> {
    return this.getModelArray(TagModel, "result");
  }
  public static parse(data: any): ArticleTagsModel {
    return new ArticleTagsModel(data);
  }
}
