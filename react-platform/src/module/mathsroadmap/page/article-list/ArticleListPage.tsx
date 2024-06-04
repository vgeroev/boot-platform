import { LikeOutlined, MessageOutlined } from "@ant-design/icons";
import { Button, Col, Divider, List, Row, Space } from "antd";
import Search from "antd/es/input/Search";
import React from "react";
import { Link } from "react-router-dom";
import { useHttpRequest } from "../../../../hook/useHttpRequestHook";
import { ArticleListModel } from "../../model/ArticleListModel";
import { ArticleModel } from "../../model/ArticleModel";
import { getArticleRoute } from "../../route/MathsRoadMapRouteGetter";
import { GetArticleListRequest } from "../../service/request/GetArticleRequestList";

const IconText = ({ icon, text }: { icon: React.FC; text: string }) => (
  <Space>
    {React.createElement(icon)}
    {text}
  </Space>
);

const MAX_PAGE_SIZE = 100;
const INCREMENT_PAGE_SIZE = 20;

type PagingFilter = {
  page: number;
  pageSize: number;
  titlePrefix?: string;
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
    if (pagingFilter.titlePrefix) {
      requestParams.titlePrefix = pagingFilter.titlePrefix;
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
              setPagingFilter({ ...pagingFilter, titlePrefix: value })
            }
          />
        </Col>
        <Col span={9}></Col>
      </Row>
      <Row>
        <Col span={6}></Col>
        <Col span={9}>
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
                      text="0"
                      key="list-vertical-like-o"
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
                    description={`${item.data.creatorUsername}, ${item.data.createdAt.split("T")[0]}`}
                  />
                  {prettifyDescription(item.data)}
                </List.Item>
              </>
            )}
          />
        </Col>
        <Col span={6}></Col>
      </Row>
    </>
  );
};

export default ArticleListPage;
