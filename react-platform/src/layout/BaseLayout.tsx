import Layout, { Content, Footer, Header } from "antd/es/layout/layout";
import BaseHeader from "./BaseHeader";
import "./BaseLayout.styles.css";

const contentProperties: React.CSSProperties = {
  height: "100vh",
};

export interface BaseLayoutProps {
  component: React.ReactElement;
}

const BaseLayout: React.FC<BaseLayoutProps> = ({
  component,
}: BaseLayoutProps) => {
  return (
    <Layout>
      <BaseHeader />
      <Content style={contentProperties}>{component}</Content>
      <Footer>Foor</Footer>
    </Layout>
  );
};

export default BaseLayout;
