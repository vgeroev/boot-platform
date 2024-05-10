import { BaseModel } from "../../../model/BaseModel";

export class PreviewArticleModel implements BaseModel {
  public get articleURL(): string {
    return this._articleURL;
  }
  constructor(private readonly _articleURL: string) {}

  public static parse(data: Record<string, unknown>): PreviewArticleModel {
    const articleURL: string | unknown = data["articleURL"];
    if (!articleURL || !(typeof articleURL === "string")) {
      throw new Error("No articleURL in " + data);
    }

    const model: PreviewArticleModel = new PreviewArticleModel(articleURL);
    return model;
  }
}
