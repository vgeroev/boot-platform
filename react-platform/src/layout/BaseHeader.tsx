import { Header } from "antd/es/layout/layout";
import LoggedUserInfo from "./LoggedUserInfo";

const hStyle: React.CSSProperties = {
  textAlign: "center",
  color: "white",
  height: 64,
  paddingInline: 50,
  lineHeight: "64px",
  backgroundColor: "#aaa",
};
const BaseHeader: React.FC<{}> = () => {
  return (
    <Header style={hStyle}>
      <LoggedUserInfo />
    </Header>
  );
};

export default BaseHeader;
