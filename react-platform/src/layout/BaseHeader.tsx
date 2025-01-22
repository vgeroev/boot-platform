import { Alert, Button } from "antd";
import React, { useContext } from "react";
import { Header } from "antd/es/layout/layout";
import LoggedUserInfo from "./LoggedUserInfo";
import { Link, useLocation, useNavigate } from "react-router-dom";
import Spinner from "../component/spinner/Spinner";
import {
  getLoginRoute,
  getRegisterRoute,
} from "../module/security/route/SecurityRoutes";
import { useHttpRequest } from "../hook/useHttpRequestHook";
import { GetLoggedInUser } from "../module/security/service/request/GetLoggedInUserRequest";
import { UserModel } from "../module/security/model/UserModel";
import { LogoutRequest } from "../module/security/service/request/LogoutRequest";
import { UserContext } from "../hook/UserContext";

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
  const { pathname } = useLocation();
  const navigate = useNavigate();
  const getLoggedInUserRequest = useHttpRequest(GetLoggedInUser);
  const logoutRequest = useHttpRequest(LogoutRequest);
  const { user, setUser } = useContext(UserContext);

  React.useEffect(() => {
    getLoggedInUserRequest.exec({
      onCompletion: (response) => {
        const status = response.status;
        if (status === 200) {
          setUser(UserModel.parse(response.data));
        } else {
          console.log("Failed to fetch user ", response);
          setUser(undefined);
        }
      },
    });
  }, []);

  let authStatusBar: React.ReactElement;
  if (user) {
    authStatusBar = (
      <LoggedUserInfo
        username={user.username}
        onLogout={() => {
          logoutRequest.exec({
            onCompletion: () => {
              navigate(getLoginRoute());
            },
          });
          setUser(undefined);
        }}
      />
    );
  } else {
    authStatusBar = (
      <>
        <Button type="primary" onClick={() => navigate(getLoginRoute())}>
          Log in
        </Button>
        <Button type="default" onClick={() => navigate(getRegisterRoute())}>
          Sign up
        </Button>
      </>
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
