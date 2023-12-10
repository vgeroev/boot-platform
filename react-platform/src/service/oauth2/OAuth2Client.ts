import axios from "axios";
import uuid from "react-uuid";
import { Jwts } from "./Jwts";

enum Client {
  SPA = "SPA",
}

abstract class OAuth2Client {
  protected jwts: Jwts | null = null;
  private readonly TOKEN_STATE_KEY = "token_state";

  constructor(
    protected readonly idpDomain: string,
    protected readonly client: Client,
    protected readonly clientSecret: string | null,
  ) { }

  protected abstract getAuthUri(state: string, redirectUri: string): string;

  protected abstract getTokenUri(): string;

  loginWithRedirect(redirectUri: string): void {
    const uuidValue: string = uuid();
    localStorage.setItem(this.TOKEN_STATE_KEY, uuidValue);
    const authUri: string = this.getAuthUri(uuidValue, redirectUri);
    window.location.replace(authUri);
  }

  fetchJwts(code: string, state: string, redirectUri: string): void {
    const initialState = localStorage.getItem(this.TOKEN_STATE_KEY);
    if (state !== initialState) {
      throw new Error(`Invalid state: ${state}`);
    }

    const urlSearchParams: URLSearchParams = new URLSearchParams();
    urlSearchParams.append("grant_type", "authorization_code");
    urlSearchParams.append("client_id", this.client);
    if (this.clientSecret !== null) {
      urlSearchParams.append("client_secret", this.clientSecret);
    }
    urlSearchParams.append("code", code);
    urlSearchParams.append("redirect_uri", encodeURI(redirectUri));

    axios
      .post(this.getTokenUri(), urlSearchParams)
      .then((response: any) => {
        console.log(response);
        this.jwts = new Jwts(
          response.data.access_token,
          response.data.refresh_token,
        );
        console.log(this.jwts);
      })
      .catch((error) => {
        console.log(error);
      });
  }

  getJwts(): Jwts | null {
    return this.jwts;
  }
}

export { OAuth2Client, Client };
