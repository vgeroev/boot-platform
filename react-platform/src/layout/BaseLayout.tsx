import Layout, { Content, Footer } from "antd/es/layout/layout";
import BaseHeader from "./BaseHeader";
import "./styles.css";

export interface BaseLayoutProps {
  component: React.ReactElement;
}

const BaseLayout: React.FC<BaseLayoutProps> = ({
  component,
}: BaseLayoutProps) => {
  return (
    <Layout>
      <BaseHeader />
      <Content className="content">{component}</Content>
      <Footer className="footer"></Footer>
    </Layout>
  );
};

export default BaseLayout;
