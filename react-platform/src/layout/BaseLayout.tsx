import Layout, { Content, Footer } from "antd/es/layout/layout";
import BaseHeader from "./BaseHeader";

const contentProperties: React.CSSProperties = {
  height: "auto",
  color: "#F3F3F3",
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
      <Footer />
    </Layout>
  );
};

export default BaseLayout;
