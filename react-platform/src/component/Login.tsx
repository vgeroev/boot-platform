import { useEffect } from "react";
import { AuthContextProps, useAuth } from "react-oidc-context";
import { buildDefaultClient } from "../service/oauth2";
import { Client, OAuth2Client } from "../service/oauth2/OAuth2Client";

const handleLoginSubmit = (event: any) => {
  event.preventDefault();

  // const oauth2Client: OAuth2Client = buildDefaultClient(Client.SPA);
  // oauth2Client.loginWithRedirect(
  //   "http://localhost:3000/authorization-redirect",
  // );
  // oauth2Client.loginWithRedirect(window.location.hostname);
};

const Login: React.FC<{}> = () => {
  return <></>;
};

export default Login;
