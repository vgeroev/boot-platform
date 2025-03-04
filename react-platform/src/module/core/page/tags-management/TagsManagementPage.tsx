import { Button, Input, List, Spin, Tag } from "antd";
import Search from "antd/es/input/Search";
import React from "react";
import WithAuthorization from "../../../../hoc/authorized/WithAuthorization";
import { useHttpRequest } from "../../../../hook/useHttpRequestHook";
import { TagListModel } from "../../model/TagListModel";
import { CreateTagRequest } from "../../request/CreateTagRequest";
import { GetTagListRequest } from "../../request/GetTagsRequest";
import { RemoveTagRequest } from "../../request/RemoveTagRequest";

interface PagingRequest {
  searchText?: string;
}

const TagsManagementPage: React.FC<{}> = () => {
  const [pagingRequest, setPagingRequest] = React.useState<
    PagingRequest | undefined
  >(undefined);
  const [updateListFlag, setUpdateListFlag] = React.useState<boolean>(false);
  const updateList = () => setUpdateListFlag(!updateListFlag);

  const [name, setName] = React.useState<string | undefined>(undefined);
  const [color, setColor] = React.useState<string>("000000");
  const [loading, setLoading] = React.useState<boolean>(false);
  const [tagListModel, setTagListModel] = React.useState<
    TagListModel | undefined
  >(undefined);
  const getTagListRequest: GetTagListRequest =
    useHttpRequest(GetTagListRequest);
  const createTagRequest: CreateTagRequest = useHttpRequest(CreateTagRequest);
  const removeTagRequest: RemoveTagRequest = useHttpRequest(RemoveTagRequest);

  const createTagAction = () => {
    createTagRequest.exec({
      data: {
        name: name,
        hexColor: color,
      },
      onSuccess: () => {
        updateList();
      },
    });
  };
  const removeTagAction = (ids: number[]) => {
    removeTagRequest.exec({
      data: ids,
      onSuccess: () => {
        updateList();
      },
    });
  };

  React.useEffect(() => {
    setLoading(true);

    const requestParams: any = {};
    if (pagingRequest?.searchText) {
      requestParams.searchText = pagingRequest.searchText;
    }

    getTagListRequest.exec({
      requestParams: requestParams,
      onSuccess: (httpResponse) => {
        setTagListModel(httpResponse.data);
      },
      onFinally: () => {
        setLoading(false);
      },
    });
  }, [pagingRequest, updateListFlag]);

  return (
    <Spin spinning={loading}>
      <Input
        placeholder="Tag Name"
        value={name}
        onChange={(e) => setName(e.target.value)}
        style={{ marginBottom: 10 }}
      />
      <Input
        type="color"
        value={"#" + color}
        onChange={(e) => setColor(e.target.value.substring(1))}
        style={{ marginBottom: 10 }}
      />
      <Button
        type="primary"
        onClick={() => {
          createTagAction();
        }}
        block
      >
        Add Tag
      </Button>

      <Search
        placeholder="Search tag"
        onSearch={(value) =>
          setPagingRequest({ ...pagingRequest, searchText: value })
        }
      />

      <List
        size="large"
        header={<div>Tag list (total count: {tagListModel?.totalCount})</div>}
        bordered
        dataSource={tagListModel?.result}
        renderItem={(item: any) => (
          <>
            <List.Item key={item.data.id}>
              <List.Item.Meta
                description={
                  <Tag color={"#" + item.data.hexColor}>{item.data.name}</Tag>
                }
              />

              <Button onClick={() => removeTagAction([item.data.id])}>
                Remove
              </Button>
            </List.Item>
          </>
        )}
      />
    </Spin>
  );
};

export default WithAuthorization(TagsManagementPage);
