import { BaseModel } from "../../../model/BaseModel";
import { ArticleWithCreatorModel } from "./ArticleWithCreatorModel";

export class GetArticleModel extends BaseModel {
    public get article(): ArticleWithCreatorModel {
        return this.getModel(ArticleWithCreatorModel, "article");
    }
    public get url(): string {
        return this.getString("url");
    }

    public static parse(data: any): GetArticleModel {
        return new GetArticleModel(data);
    }
}
