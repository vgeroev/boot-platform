import { LikeOutlined, MessageOutlined } from "@ant-design/icons";
import { Button, Col, Divider, List, Row, Space } from "antd";
import Search from "antd/es/input/Search";
import React from "react";
import { Link } from "react-router-dom";
import { useHttpRequest } from "../../../../hook/useHttpRequestHook";
import { RoadMapListModel } from "../../model/RoadMapListModel";
import { RoadMapModel } from "../../model/RoadMapModel";
import {
  getArticleListRoute,
  getRoadMapRoute,
} from "../../route/MathsRoadMapRouteGetter";
import { GetRoadMapListRequest } from "../../service/request/GetRoadMapListRequest";

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

function prettifyDescription(roadMapModel: RoadMapModel): string | null {
  if (!roadMapModel.description) {
    return null;
  }

  const description: string = roadMapModel.description;
  if (description.length < 255) {
    return description;
  }

  return description.slice(0, 255) + "...";
}

function getRoadMapMeta(roadMap: RoadMapModel): string {
  const baseMetaMsg: string = `${roadMap.creatorUsername}, ${roadMap.createdAt.toString().split("T")[0]}`;
  if (roadMap.updatedAt && roadMap.createdAt !== roadMap.updatedAt) {
    return (
      baseMetaMsg + ` (updated: ${roadMap.updatedAt.toString().split("T")[0]})`
    );
  }
  return baseMetaMsg;
}

const RoadMapListPage: React.FC<{}> = () => {
  const [pagingFilter, setPagingFilter] = React.useState<PagingFilter>({
    page: 0,
    pageSize: 10,
  });
  const [loading, setLoading] = React.useState<boolean>(false);
  const [roadMapListModel, setRoadMapListModel] = React.useState<
    RoadMapListModel | undefined
  >(undefined);
  const getRoadMapListRequest: GetRoadMapListRequest = useHttpRequest(
    GetRoadMapListRequest,
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

    getRoadMapListRequest.exec({
      requestParams: requestParams,
      onSuccess: (httpResponse) => {
        setRoadMapListModel(httpResponse.data);
      },
      onFinally: () => {
        setLoading(false);
      },
    });
  }, [pagingFilter]);

  const loadMore =
    !loading &&
    pagingFilter.pageSize < (roadMapListModel?.totalCount || 0) &&
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
      <Divider orientation="left">
        <Link to={getArticleListRoute()}>Link to Articles</Link>
      </Divider>
      <Divider>Road maps</Divider>
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
        <Col span={8}></Col>
        <Col span={8}>
          <List
            loading={loading}
            loadMore={loadMore}
            itemLayout="vertical"
            size="large"
            dataSource={roadMapListModel?.result}
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
                      <Link to={getRoadMapRoute(item.data.id)}>
                        {item.data.title}
                      </Link>
                    }
                    description={getRoadMapMeta(item.data)}
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

export default RoadMapListPage;
