export abstract class BaseModel {}

export interface IModelParser {
  new (...args: any[]): any;
  parse(data: Record<string, unknown>): InstanceType<this>;
}

export class ModelFactory<
  I extends IModelParser,
  M extends BaseModel = InstanceType<I>,
> {
  constructor(private readonly model: I) {}

  public getModel(data: Record<string, unknown>): M {
    return this.model.parse(data);
  }
}
