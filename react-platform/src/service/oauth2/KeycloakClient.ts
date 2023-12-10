import { Client, OAuth2Client } from "./OAuth2Client";

class KeycloakClient extends OAuth2Client {
  constructor(
    idpDomain: string,
    client: Client,
    clientSecret: string | null,
    private readonly realm: string,
  ) {
    super(idpDomain, client, clientSecret);
  }

  protected getAuthUri(state: string, redirectUri: string): string {
    return (
      `${this.idpDomain}/realms/${this.realm}/protocol/openid-connect/auth` +
      `?response_type=code&client_id=${encodeURI(
        this.client,
      )}&redirect_uri=${encodeURI(redirectUri)}&state=${encodeURI(state)}`
    );
  }

  protected getTokenUri(): string {
    return `${this.idpDomain}/realms/${this.realm}/protocol/openid-connect/token`;
  }
}

export default KeycloakClient;
