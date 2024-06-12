import { BaseModel } from "../../../model/BaseModel";
import { ArticleModel } from "./ArticleModel";
import { RoadMapModel } from "./RoadMapModel";
import { RoadMapTreeEdge } from "./RoadMapTreeEdgeModel";

export class RoadMapTreeModel extends BaseModel {
  public get roadMap(): RoadMapModel {
    return this.getModel(RoadMapModel, "roadMap");
  }

  public get articles(): Array<ArticleModel> {
    return this.getModelArray(ArticleModel, "articles");
  }

  public get tree(): Array<RoadMapTreeEdge> {
    return this.getModelArray(RoadMapTreeEdge, "tree");
  }

  public static parse(data: any): RoadMapTreeModel {
    return new RoadMapTreeModel(data);
  }
}
