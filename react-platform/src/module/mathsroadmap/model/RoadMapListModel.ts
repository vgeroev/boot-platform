import { IModelParser } from "../../../model/BaseModel";
import { PaginatedModel } from "../../../model/PaginatedModel";
import { RoadMapModel } from "./RoadMapModel";

export class RoadMapListModel extends PaginatedModel<RoadMapModel> {
  public getPaginatedModel(): IModelParser {
    return RoadMapListModel;
  }

  public static parse(data: any): RoadMapListModel {
    return new RoadMapListModel(data);
  }
}
