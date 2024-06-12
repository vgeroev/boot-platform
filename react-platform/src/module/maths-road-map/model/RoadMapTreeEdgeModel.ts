import { BaseModel } from "../../../model/BaseModel";

export class RoadMapTreeEdge extends BaseModel {
  public get id(): number {
    return this.getNumber("id");
  }

  public get roadMapId(): number {
    return this.getNumber("roadMapId");
  }

  public get prevArticleId(): number {
    return this.getNumber("prevArticleId");
  }

  public get nextArticleId(): number {
    return this.getNumber("nextArticleId");
  }

  public static parse(data: any): RoadMapTreeEdge {
    return new RoadMapTreeEdge(data);
  }
}
