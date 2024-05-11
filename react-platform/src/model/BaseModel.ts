export abstract class BaseModel {
  protected readonly data: any;

  constructor(data: any) {
    if (!data.data) {
      throw new Error("No data field in " + data);
    }
    this.data = data.data;
  }

  public getStringOptional(fieldName?: string): string | undefined {
    return this.getValue<string>(
      (v) => BaseModel.parsePrimitive<string>(v, "string"),
      true,
      fieldName,
    );
  }

  public getString(fieldName?: string): string {
    return this.getValue<string>(
      (v) => BaseModel.parsePrimitive<string>(v, "string"),
      false,
      fieldName,
    )!;
  }

  public getStringArrayOptional(fieldName?: string): Array<string> | undefined {
    return this.getArrayValue(
      (v) => BaseModel.parsePrimitive<string>(v, "string"),
      true,
      fieldName,
    );
  }

  public getStringArray(fieldName?: string): Array<string> {
    return this.getArrayValue(
      (v) => BaseModel.parsePrimitive<string>(v, "string"),
      false,
      fieldName,
    )!;
  }

  public getNumberOptional(fieldName?: string): number | undefined {
    return this.getValue<number>(
      (v) => BaseModel.parsePrimitive<number>(v, "number"),
      true,
      fieldName,
    );
  }

  public getNumber(fieldName?: string): number {
    return this.getValue<number>(
      (v) => BaseModel.parsePrimitive<number>(v, "number"),
      false,
      fieldName,
    )!;
  }

  public getNumberArrayOptional(fieldName?: string): Array<number> | undefined {
    return this.getArrayValue(
      (v) => BaseModel.parsePrimitive<number>(v, "number"),
      true,
      fieldName,
    );
  }

  public getNumberArray(fieldName?: string): Array<number> | undefined {
    return this.getArrayValue(
      (v) => BaseModel.parsePrimitive<number>(v, "number"),
      false,
      fieldName,
    );
  }

  public getBooleanOptional(fieldName?: string): boolean | undefined {
    return this.getValue<boolean>(
      (v) => BaseModel.parsePrimitive<boolean>(v, "boolean"),
      true,
      fieldName,
    );
  }

  public getBoolean(fieldName?: string): boolean {
    return this.getValue<boolean>(
      (v) => BaseModel.parsePrimitive<boolean>(v, "boolean"),
      false,
      fieldName,
    )!;
  }

  public getModelOptional<T extends BaseModel>(
    model: IModelParser,
    fieldName?: string,
  ): T | undefined {
    return this.getValue<T>((v) => model.parse(v), true, fieldName);
  }

  public getModel<T extends BaseModel>(
    model: IModelParser,
    fieldName?: string,
  ): T {
    return this.getValue<T>((v) => model.parse(v), false, fieldName)!;
  }

  public getModelArrayOptional<T extends BaseModel>(
    model: IModelParser,
    fieldName?: string,
  ): Array<T> | undefined {
    return this.getArrayValue<T>((v) => model.parse(v), true, fieldName);
  }

  public getModelArray<T extends BaseModel>(
    model: IModelParser,
    fieldName?: string,
  ): Array<T> {
    return this.getArrayValue<T>((v) => model.parse(v), false, fieldName)!;
  }

  private getArrayValue<T>(
    parser: (v: any) => T | undefined,
    optional: boolean,
    fieldName?: string,
  ): Array<T> | undefined {
    return this.getValue<Array<T>>(
      (v) => {
        if (!Array.isArray(v)) {
          return undefined;
        }

        return v.map((value) => {
          const parsed: T | undefined = parser(value);
          if (!parsed) {
            throw new Error("Failed to parse array element: " + value);
          }
          return parsed;
        });
      },
      optional,
      fieldName,
    );
  }

  private static parsePrimitive<T>(value: any, type: string): T | undefined {
    return typeof value === type ? value : undefined;
  }

  private getValue<T>(
    parser: (v: any) => T | undefined,
    optional: boolean,
    fieldName?: string,
  ): T | undefined {
    if (optional && (!fieldName || !(fieldName in this.data))) {
      return undefined;
    }
    const value: any = fieldName ? this.data[fieldName] : this.data;
    if (value) {
      const parsed: T | undefined = parser(value);
      if (parsed) {
        return parsed;
      }
    }

    throw new Error(
      `No required field or field has invalid type: '${fieldName}'`,
    );
  }
}

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
