import React from "react";
import ReactDOM from "react-dom/client";
import { AuthProvider } from "react-oidc-context";
import App from "./App";

const oidcConfig = {
  authority: "http://localhost:8080/realms/boot-platform",
  client_id: "SPA",
  client_secret: "AsliLkyrAxKycByry4DvaFjwhkjZVcKV",
  redirect_uri: "http://localhost:3000/authorization-redirect",
  onSigninCallback: () => {
    window.history.replaceState({}, document.title, window.location.pathname);
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
