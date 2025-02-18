import { DislikeOutlined, LikeOutlined } from "@ant-design/icons";
import { Button, Col, Divider, Modal, Row, Spin } from "antd";
import Title from "antd/es/typography/Title";
import React, { useContext } from "react";
import Iframe from "react-iframe";
import { Link, useParams } from "react-router-dom";
import { useHttpRequest } from "../../../../hook/useHttpRequestHook";
import { UserContext } from "../../../../hook/UserContext";
import { UserModel } from "../../../security/model/UserModel";
import { ArticleLikeAction } from "../../model/ArticleLikeAction";
import { GetArticleModel } from "../../model/GetArticleModel";
import { getUpdateArticleRoute } from "../../route/MathsRoadMapRouteGetter";
import { GetArticleLikeActionRequest } from "../../service/request/GetArticleLikeActionRequest";
import { GetArticleRequest } from "../../service/request/GetArticleRequest";
import { SubmitArticleDislikeRequest } from "../../service/request/SubmitArticleDislikeRequest";
import { SubmitArticleLikeRequest } from "../../service/request/SubmitArticleLikeRequest";
import "./styles.css";

const ArticlePage: React.FC<{}> = () => {
  const { user: loggedInUser } = useContext(UserContext);
  const [loading, setLoading] = React.useState<boolean>(false);
  const { id } = useParams();
  const articleId: number = Number(id);
  const [getArticleModel, setGetArticleModel] = React.useState<
    GetArticleModel | undefined
  >(undefined);
  const [likeAction, setLikeAction] = React.useState<
    ArticleLikeAction | undefined
  >(undefined);

  const getArticleRequest: GetArticleRequest =
    useHttpRequest(GetArticleRequest);
  const getArticleLikeActionRequest: GetArticleLikeActionRequest =
    useHttpRequest(GetArticleLikeActionRequest);
  const submitLikeRequest: SubmitArticleLikeRequest = useHttpRequest(
    SubmitArticleLikeRequest,
  );
  const submitDislikeRequest: SubmitArticleDislikeRequest = useHttpRequest(
    SubmitArticleDislikeRequest,
  );

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

    if (loggedInUser) {
      getArticleLikeActionRequest.exec({
        requestVariables: {
          id: articleId,
        },
        onSuccess: (httpResponse) => {
          setLikeAction(httpResponse.data.action);
        },
      });
    }
  }, []);

  return (
    <Spin spinning={loading}>
      <Row>
        <Col flex="auto">
          <Title level={2}>{getArticleModel?.article.title}</Title>
        </Col>
        {getEditButton(articleId, loggedInUser, getArticleModel)}
      </Row>
      <Title level={5}>{getArticleModel?.article.creator.username}</Title>
      <Title level={5}>
        {getArticleModel?.article.createdAt.toUTCString()}
      </Title>
      {getArticleModel?.article.description || ""}
      <Title>
        <Row justify="start" gutter={16}>
          <Col>
            <Button
              type={
                likeAction === ArticleLikeAction.LIKED ? "primary" : "default"
              }
              icon={<LikeOutlined />}
              onClick={() =>
                handleLike(articleId, submitLikeRequest, (action) =>
                  setLikeAction(action),
                )
              }
            >
              Like
            </Button>
          </Col>
          <Col>
            <Button
              type={
                likeAction === ArticleLikeAction.DISLIKED
                  ? "primary"
                  : "default"
              }
              icon={<DislikeOutlined />}
              onClick={() =>
                handleDislike(articleId, submitDislikeRequest, (action) =>
                  setLikeAction(action),
                )
              }
            >
              Dislike
            </Button>
          </Col>
        </Row>
      </Title>
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

const handleLike = (
  articleId: number,
  request: SubmitArticleLikeRequest,
  likeSetter: (action: ArticleLikeAction) => void,
) => {
  request.exec({
    requestVariables: {
      id: articleId,
    },
    onSuccess: (httpResponse) => {
      likeSetter(httpResponse.data.action);
    },
  });
};

const handleDislike = (
  articleId: number,
  request: SubmitArticleDislikeRequest,
  likeSetter: (action: ArticleLikeAction) => void,
) => {
  request.exec({
    requestVariables: {
      id: articleId,
    },
    onSuccess: (httpResponse) => {
      likeSetter(httpResponse.data.action);
    },
  });
};

function getEditButton(
  articleId: number,
  loggedInUser: UserModel | undefined,
  getArticleModel: GetArticleModel | undefined,
) {
  if (loggedInUser?.id === getArticleModel?.article.creator.id) {
    return (
      <Col flex="100px">
        <Title level={4}>
          <Link to={getUpdateArticleRoute(articleId)}>Edit</Link>
        </Title>
      </Col>
    );
  }

  return <></>;
}

export default ArticlePage;
