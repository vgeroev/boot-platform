import React from "react";
import ReactDOM from "react-dom/client";
import { AuthProvider } from "react-oidc-context";
import App from "./App";
import { getHomeUrl } from "./utils/UrlUtils";

const env = process.env;
const oidcConfig = {
  authority: env.REACT_APP_KEYCLOAK_AUTHORITY,
  client_id: env.REACT_APP_KEYCLOAK_CLIENT_ID,
  client_secret: env.REACT_APP_KEYCLOAK_CLIENT_SECRET,
  redirect_uri: env.REACT_APP_KEYCLOAK_REDIRECT_URI,
  onSigninCallback: () => {
    window.history.replaceState({}, document.title, window.location.pathname);
    window.location.href = getHomeUrl();
  },
};

const root = ReactDOM.createRoot(document.getElementById("root")!);
root.render(
  <React.StrictMode>
    <AuthProvider {...oidcConfig}>
      <App />
    </AuthProvider>
  </React.StrictMode>,
);
