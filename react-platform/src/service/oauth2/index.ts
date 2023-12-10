import { Jwts } from "./Jwts";
import KeycloakClient from "./KeycloakClient";
import { Client, OAuth2Client } from "./OAuth2Client";

const getClientSecret = (client: Client): string | null => {
  if (client === Client.SPA) {
    return "AsliLkyrAxKycByry4DvaFjwhkjZVcKV";
  }

  return null;
};

const defaultClientGetter = (client: Client): OAuth2Client => {
  return new KeycloakClient(
    "http://localhost:8080",
    client,
    getClientSecret(client),
    "boot-platform",
  );
};

export { Jwts, OAuth2Client, defaultClientGetter as buildDefaultClient };
