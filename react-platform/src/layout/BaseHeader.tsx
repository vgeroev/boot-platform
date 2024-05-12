import { Alert, Button } from "antd";
import React from "react";
import { Header } from "antd/es/layout/layout";
import { AuthContextProps, useAuth } from "react-oidc-context";
import LoggedUserInfo from "./LoggedUserInfo";
import { Link, useLocation } from "react-router-dom";
import Spinner from "../component/spinner/Spinner";

const hStyle: React.CSSProperties = {
  textAlign: "center",
  color: "white",
  // height: 64,
  // paddingInline: 50,
  // lineHeight: "64px",
  backgroundColor: "black",
  fontSize: 24,
  // margin: "0 auto",
  // display: "flex",
  // flexDirection: "row",
  // justifyContent: "space-between",
  // alignItems: "center",
  width: "100%",
  // position: "sticky",
  top: 0,
  zIndex: 1,
  // width: "100%",
  // display: "flex",
  // alignItems: "center",
  // backgroundColor: "#59788E",
};

function getHeaderTitle(pathname: string): string {
  if (pathname.startsWith("/exercises")) {
    return "Exercises";
  }

  return "Boot platform";
}

const BaseHeader: React.FC<{}> = () => {
  const auth: AuthContextProps = useAuth();
  const { pathname } = useLocation();

  if (auth.isLoading || auth.error) {
    return <Spinner />;
  } else if (auth.error) {
    console.error("Auth error", auth.error);
    return <Alert message={auth.error} type="error" showIcon />;
  }

  let authStatusBar: React.ReactElement;
  if (auth.isAuthenticated) {
    const username: string = auth.user?.profile.preferred_username || "";
    const onLogout = () => {
      auth.removeUser();
      auth.signoutRedirect();
    };

    authStatusBar = <LoggedUserInfo username={username} onLogout={onLogout} />;
  } else {
    authStatusBar = (
      <Button type="primary" onClick={() => void auth.signinRedirect()}>
        Log in
      </Button>
    );
  }

  return (
    <Header style={hStyle}>
      <Link to="/home">{getHeaderTitle(pathname)}</Link>
      <div
        style={{
          float: "right",
        }}
      >
        {authStatusBar}
      </div>
    </Header>
  );
};

export default BaseHeader;
