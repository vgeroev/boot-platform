import {
  DislikeOutlined,
  LikeOutlined,
  MessageOutlined,
} from "@ant-design/icons";
import { Button, Col, Divider, List, Row, Space } from "antd";
import Search from "antd/es/input/Search";
import React from "react";
import { Link } from "react-router-dom";
import { useHttpRequest } from "../../../../hook/useHttpRequestHook";
import { ArticleListModel } from "../../model/ArticleListModel";
import { ArticleModel } from "../../model/ArticleModel";
import { ArticleWithCreatorModel } from "../../model/ArticleWithCreatorModel";
import { getArticleRoute } from "../../route/MathsRoadMapRouteGetter";
import { GetArticleListRequest } from "../../service/request/GetArticleListRequest";

const IconText = ({
  icon,
  text,
  style,
}: {
  icon: React.FC;
  text: string;
  style?: React.CSSProperties;
}) => (
  <Space style={style}>
    {React.createElement(icon)}
    {text}
  </Space>
);

const MAX_PAGE_SIZE = 100;
const INCREMENT_PAGE_SIZE = 20;

type PagingFilter = {
  page: number;
  pageSize: number;
  searchText?: string;
};

function prettifyDescription(article: ArticleModel): string | null {
  if (!article.description) {
    return null;
  }

  const description: string = article.description;
  if (description.length < 255) {
    return description;
  }

  return description.slice(0, 255) + "...";
}

function getArticleMeta(article: ArticleWithCreatorModel): string {
  const baseMetaMsg: string = `${article.creator.username}, ${article.createdAt.toString().split("T")[0]}`;
  if (article.updatedAt && article.createdAt !== article.updatedAt) {
    return (
      baseMetaMsg + ` (updated: ${article.updatedAt.toString().split("T")[0]})`
    );
  }
  return baseMetaMsg;
}

const ArticleListPage: React.FC<{}> = () => {
  const [pagingFilter, setPagingFilter] = React.useState<PagingFilter>({
    page: 0,
    pageSize: 10,
  });
  const [loading, setLoading] = React.useState<boolean>(false);
  const [articleListModel, setArticleListModel] = React.useState<
    ArticleListModel | undefined
  >(undefined);
  const getArticleListRequest: GetArticleListRequest = useHttpRequest(
    GetArticleListRequest,
  );

  React.useEffect(() => {
    setLoading(true);

    const requestParams: any = {
      page: pagingFilter.page,
      pageSize: pagingFilter.pageSize,
      sortField: "CREATED_AT",
      sortDirection: "DESC",
    };
    if (pagingFilter.searchText) {
      requestParams.searchText = pagingFilter.searchText;
    }

    getArticleListRequest.exec({
      requestParams: requestParams,
      onSuccess: (httpResponse) => {
        setArticleListModel(httpResponse.data);
      },
      onFinally: () => {
        setLoading(false);
      },
    });
  }, [pagingFilter]);

  const loadMore =
    !loading &&
    pagingFilter.pageSize < (articleListModel?.totalCount || 0) &&
    pagingFilter.pageSize <= MAX_PAGE_SIZE - INCREMENT_PAGE_SIZE ? (
      <div
        style={{
          textAlign: "center",
          marginTop: 12,
          height: 32,
          lineHeight: "32px",
        }}
      >
        <Button
          onClick={() => {
            setPagingFilter({
              ...pagingFilter,
              pageSize: pagingFilter.pageSize + INCREMENT_PAGE_SIZE,
            });
          }}
        >
          More
        </Button>
      </div>
    ) : null;

  return (
    <>
      <Divider>Articles</Divider>
      <Row>
        <Col span={9}></Col>
        <Col span={6}>
          <Search
            placeholder="Title"
            loading={loading}
            onSearch={(value) =>
              setPagingFilter({ ...pagingFilter, searchText: value })
            }
          />
        </Col>
        <Col span={9}></Col>
      </Row>
      <Row>
        <Col span={8}></Col>
        <Col span={8}>
          <List
            loading={loading}
            loadMore={loadMore}
            itemLayout="vertical"
            size="large"
            dataSource={articleListModel?.result}
            // footer={
            //   <div>
            //     <b>ant design</b> footer part
            //   </div>
            // }
            renderItem={(item: any) => (
              <>
                <List.Item
                  key={item.data.id}
                  actions={[
                    <IconText
                      icon={LikeOutlined}
                      text={item.data.likes}
                      key="list-vertical-like-o"
                      style={{
                        color: getLikesColor(
                          item.data.likes,
                          item.data.dislikes,
                        ),
                      }}
                    />,
                    <IconText
                      icon={DislikeOutlined}
                      text={item.data.dislikes}
                      key="list-vertical-dislike-o"
                      style={{
                        color: getDislikesColor(
                          item.data.likes,
                          item.data.dislikes,
                        ),
                      }}
                    />,
                    <IconText
                      icon={MessageOutlined}
                      text="0"
                      key="list-vertical-message"
                    />,
                  ]}
                >
                  <List.Item.Meta
                    title={
                      <Link to={getArticleRoute(item.data.id)}>
                        {item.data.title}
                      </Link>
                    }
                    description={getArticleMeta(item.data)}
                  />
                  {prettifyDescription(item.data)}
                </List.Item>
              </>
            )}
          />
        </Col>
        <Col span={8}></Col>
      </Row>
    </>
  );
};

function getLikesColor(likes: number, dislikes: number): string {
  if (likes > dislikes) {
    return "green";
  }
  return "grey";
}

function getDislikesColor(likes: number, dislikes: number): string {
  if (likes < dislikes) {
    return "red";
  }
  return "grey";
}

export default ArticleListPage;
