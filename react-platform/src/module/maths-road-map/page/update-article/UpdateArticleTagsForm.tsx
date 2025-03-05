import { PlusOutlined } from "@ant-design/icons";
import { Button, Form, List, Space, Tag } from "antd";
import Search from "antd/es/transfer/search";
import React from "react";
import { useHttpRequest } from "../../../../hook/useHttpRequestHook";
import { TagListModel } from "../../../core/model/TagListModel";
import { TagModel } from "../../../core/model/TagModel";
import { GetTagListRequest } from "../../../core/request/GetTagsRequest";
import { AddTagToArticleRequest } from "../../service/request/AddTagToArticleRequest";
import { GetTagsByArticleRequest } from "../../service/request/GetTagsByArticleRequest";
import { RemoveTagFromArticleRequest } from "../../service/request/RemoveTagFromArticleRequest";

export interface Props {
  articleId: number;
}

const UpdateArticleTagsForm: React.FC<Props> = ({ articleId }: Props) => {
  const [searchTagText, setSearchTagText] = React.useState<string>("");
  const [foundTags, setFoundTags] = React.useState<TagListModel | undefined>(
    undefined,
  );
  const [tags, setTags] = React.useState<Array<TagModel>>([]);
  const [getTagsTrigger, setGetTagsTrigger] = React.useState<boolean>(false);
  const triggerGetTags = () => setGetTagsTrigger(!getTagsTrigger);
  const getTagsRequest: GetTagsByArticleRequest = useHttpRequest(
    GetTagsByArticleRequest,
  );
  const addTagRequest: AddTagToArticleRequest = useHttpRequest(
    AddTagToArticleRequest,
  );
  const removeTagRequest: RemoveTagFromArticleRequest = useHttpRequest(
    RemoveTagFromArticleRequest,
  );
  const getTagListRequest: GetTagListRequest =
    useHttpRequest(GetTagListRequest);

  const handleAddTag = (tagId: number) => {
    addTagRequest.exec({
      requestVariables: {
        articleId: articleId,
        tagId: tagId,
      },
      onFinally: () => triggerGetTags(),
    });
  };

  const handleRemoveTag = (tagId: number) => {
    removeTagRequest.exec({
      requestVariables: {
        articleId: articleId,
        tagId: tagId,
      },
      onFinally: () => triggerGetTags(),
    });
  };

  React.useEffect(() => {
    getTagsRequest.exec({
      requestVariables: {
        articleId: articleId,
      },
      onSuccess: (httpResponse) => {
        setTags(httpResponse.data.result);
      },
    });
  }, [getTagsTrigger]);

  React.useEffect(() => {
    const requestParams: any = {};
    if (searchTagText) {
      requestParams.searchText = searchTagText;
    }

    getTagListRequest.exec({
      requestParams: requestParams,
      onSuccess: (httpResponse) => {
        setFoundTags(httpResponse.data);
      },
    });
  }, [searchTagText]);

  return (
    <Form layout="vertical">
      <Form.Item label="Article Tags">
        <Space wrap>
          {tags.map((tag) => (
            <Tag
              key={tag.id}
              color={"#" + tag.hexColor}
              closable
              onClose={() => handleRemoveTag(tag.id)}
            >
              {tag.name}
            </Tag>
          ))}
        </Space>
      </Form.Item>
      <Form.Item>
        <Search
          placeholder="Enter a tag name"
          value={searchTagText}
          onChange={(e) => {
            setSearchTagText(e.target.value);
          }}
        />
        <List
          bordered
          dataSource={foundTags?.result}
          renderItem={(item: any) => (
            <List.Item key={item.data.id}>
              <List.Item.Meta
                description={
                  <Tag color={"#" + item.data.hexColor}>{item.data.name}</Tag>
                }
              />

              <Button
                type="primary"
                icon={<PlusOutlined />}
                onClick={() => {
                  handleAddTag(item.data.id);
                }}
              >
                Add
              </Button>
            </List.Item>
          )}
        />
      </Form.Item>
      <Form.Item></Form.Item>
    </Form>
  );
};

export default UpdateArticleTagsForm;
