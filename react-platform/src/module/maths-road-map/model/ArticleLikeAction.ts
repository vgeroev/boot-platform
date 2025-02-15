import { BaseModel } from "../../../model/BaseModel";

export enum ArticleLikeAction {
  NO_ACTION = "NO_ACTION",
  LIKED = "LIKED",
  DISLIKED = "DISLIKED",
}

export class ArticleLikeActionModel extends BaseModel {
  public get action(): ArticleLikeAction {
    let rawValue: string = this.getString("like_action");
    return ArticleLikeAction[rawValue as keyof typeof ArticleLikeAction];
  }

  public static parse(data: any): ArticleLikeActionModel {
    return new ArticleLikeActionModel(data);
  }
}
