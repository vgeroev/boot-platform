import { Col, Divider, Row, Spin } from "antd";
import Title from "antd/es/typography/Title";
import React from "react";
import { useParams } from "react-router-dom";
import { useHttpRequest } from "../../../../hook/useHttpRequestHook";
import ArticlesTreeComponent from "../../component/articles-tree/ArticlesTreeComponent";
import { RoadMapModel } from "../../model/RoadMapModel";
import { RoadMapTreeModel } from "../../model/RoadMapTreeModel";
import { GetRoadMapTreeRequest } from "../../service/request/GetRoadMapTreeRequest";

const RoadMapPage: React.FC<{}> = () => {
  const [loading, setLoading] = React.useState<boolean>(false);
  const { id } = useParams();
  const roadMapId: number = Number(id);
  const [roadMapTree, setRoadMapTree] = React.useState<
    RoadMapTreeModel | undefined
  >(undefined);
  const getRoadMapTreeRequest: GetRoadMapTreeRequest = useHttpRequest(
    GetRoadMapTreeRequest,
  );

  React.useEffect(() => {
    setLoading(true);

    getRoadMapTreeRequest.exec({
      requestVariables: {
        id: roadMapId,
      },
      onSuccess: (httpResponse) => {
        setRoadMapTree(httpResponse.data);
      },
      onFinally: () => {
        setLoading(false);
      },
    });
  }, []);

  const roadMap: RoadMapModel | undefined = roadMapTree?.roadMap;

  return (
    <Spin spinning={loading}>
      <Row>
        <Col flex="auto">
          <Title level={2}>{roadMap?.title}</Title>
        </Col>
      </Row>
      <Title level={5}>{roadMap?.creator.username}</Title>
      <Title level={5}>{roadMap?.createdAt.toUTCString()}</Title>
      {roadMap?.description}
      <Divider>Articles tree</Divider>
      <ArticlesTreeComponent edges={[]} />
    </Spin>
  );
};

export default RoadMapPage;
