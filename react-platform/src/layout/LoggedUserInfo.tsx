import { UserOutlined } from "@ant-design/icons";
import { Button } from "antd";

interface Props {
  username: string;
  onLogout: () => void;
}

const LoggedUserInfo: React.FC<Props> = ({ username, onLogout }: Props) => {
  // switch (auth.activeNavigator) {
  //   case "signinSilent":
  //     return <div>Signing you in...</div>;
  //   case "signoutRedirect":
  //     return <div>Signing you out...</div>;
  // }
  //

  //
  // if (auth.error) {
  //   return <div>Oops... {auth.error.message}</div>;
  // }
  //

  return (
    <div
      style={{
        margin: "0 auto",
        display: "flex",
        gap: "10px",
        flexDirection: "row",
        justifyContent: "space-between",
        alignItems: "center",
      }}
    >
      <UserOutlined />
      {username}
      <Button type="primary" onClick={onLogout}>
        Log out
      </Button>
    </div>
  );
};

export default LoggedUserInfo;
