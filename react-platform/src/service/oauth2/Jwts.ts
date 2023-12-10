export class Jwts {
  private readonly _accessToken: string;
  private readonly _refreshToken: string;

  constructor(accessToken: string, refreshToken: string) {
    this._accessToken = accessToken;
    this._refreshToken = refreshToken;
  }

  public get accessToken(): string {
    return this._accessToken;
  }

  public get refreshToken(): string {
    return this._refreshToken;
  }
}
