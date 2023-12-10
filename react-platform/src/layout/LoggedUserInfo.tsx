import { Spin } from "antd";
import { useAuth } from "react-oidc-context";

const LoggedUserInfo: React.FC<{}> = () => {
  const auth = useAuth();

  // switch (auth.activeNavigator) {
  //   case "signinSilent":
  //     return <div>Signing you in...</div>;
  //   case "signoutRedirect":
  //     return <div>Signing you out...</div>;
  // }
  //
  if (auth.isLoading || auth.error) {
    return <Spin></Spin>;
  }
  //
  // if (auth.error) {
  //   return <div>Oops... {auth.error.message}</div>;
  // }

  if (auth.isAuthenticated) {
    return (
      <div>
        {auth.user?.profile.preferred_username}{" "}
        <button onClick={() => void auth.removeUser()}>Log out</button>
      </div>
    );
  } else {
    return (
      <div>
        <button onClick={() => void auth.signinRedirect()}>Log in</button>;
      </div>
    );
  }
};

export default LoggedUserInfo;
