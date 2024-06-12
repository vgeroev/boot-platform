import { Col, Divider, Modal, Row, Spin } from "antd";
import Title from "antd/es/typography/Title";
import React from "react";
import Iframe from "react-iframe";
import { Link, useParams } from "react-router-dom";
import { useHttpRequest } from "../../../../hook/useHttpRequestHook";
import { GetArticleModel } from "../../model/GetArticleModel";
import { getUpdateArticleRoute } from "../../route/MathsRoadMapRouteGetter";
import { GetArticleRequest } from "../../service/request/GetArticleRequest";
import "./styles.css";

const ArticlePage: React.FC<{}> = () => {
  const [loading, setLoading] = React.useState<boolean>(false);
  const { id } = useParams();
  const articleId: number = Number(id);
  const [getArticleModel, setGetArticleModel] = React.useState<
    GetArticleModel | undefined
  >(undefined);
  const getArticleRequest: GetArticleRequest =
    useHttpRequest(GetArticleRequest);

  React.useEffect(() => {
    setLoading(true);

    getArticleRequest.exec({
      requestVariables: {
        id: articleId,
      },
      onSuccess: (httpResponse) => {
        setGetArticleModel(httpResponse.data);
      },
      handleModuleError: (e) => {
        switch (e.data.code) {
          case "not_found_domain_object":
            Modal.error({
              title: "Article cannot be found by id=" + articleId,
            });
            return true;
          default:
            return false;
        }
      },
      onFinally: () => {
        setLoading(false);
      },
    });
  }, []);

  return (
    <Spin spinning={loading}>
      <Row>
        <Col flex="auto">
          <Title level={2}>{getArticleModel?.article.title}</Title>
        </Col>
        <Col flex="100px">
          <Title level={4}>
            <Link to={getUpdateArticleRoute(articleId)}>Edit</Link>
          </Title>
        </Col>
      </Row>
      <Title level={5}>{getArticleModel?.article.creatorUsername}</Title>
      <Title level={5}>
        {getArticleModel?.article.createdAt.toUTCString()}
      </Title>
      {getArticleModel?.article.description || ""}
      <Divider></Divider>
      {/* <div className="iframe-div"> */}
      <Iframe
        // className="previewArticle"
        // allowFullScreen
        url={getArticleModel?.url || ""}
        // frameBorder={0}
        // height="100%"
        // width="100%"
        // scrolling="auto"
        // overflow="auto"
        className="iframe-article"
      // scrolling="no"
      />
      {/* </div> */}
    </Spin>
  );
};

export default ArticlePage;
