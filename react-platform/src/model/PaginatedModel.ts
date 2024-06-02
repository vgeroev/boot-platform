import { BaseModel, IModelParser } from "./BaseModel";

export abstract class PaginatedModel<T extends BaseModel> extends BaseModel {
  public get result(): Array<T> {
    return this.getModelArray(this.getPaginatedModel(), "result");
  }

  public get totalCount(): number {
    return this.getNumber("totalCount");
  }

  public get page(): number {
    return this.getNumber("page");
  }

  public get pageSize(): number {
    return this.getNumber("pageSize");
  }

  public abstract getPaginatedModel(): IModelParser;
}
