import { Form, Input, Button, Row, Col, Spin, Divider } from "antd";
import React from "react";
import { useHttpRequest } from "../../../../hook/useHttpRequestHook";
import WithAuthorization from "../../../../hoc/authorized/WithAuthorization";
import { useNavigate, useParams } from "react-router-dom";
import { getArticleRoute } from "../../route/MathsRoadMapRouteGetter";
import TextArea from "antd/es/input/TextArea";
import { UpdateArticleRequest } from "../../service/request/UpdateArticleRequest";
import { GetArticleRequest } from "../../service/request/GetArticleRequest";

interface UpdateArticleForm {
  title: string;
  description: string | null;
}

const UpdateArticlePage: React.FC<{}> = () => {
  const { id } = useParams();
  const identifier: number = Number(id);
  const [updateArticleForm, setUpdateArticleForm] = React.useState<
    UpdateArticleForm | undefined
  >(undefined);
  const [loading, setLoading] = React.useState<boolean>(false);
  const getArticleRequest: GetArticleRequest =
    useHttpRequest(GetArticleRequest);
  const updateArticleRequest: UpdateArticleRequest =
    useHttpRequest(UpdateArticleRequest);
  const navigate = useNavigate();

  React.useEffect(() => {
    setLoading(true);
    getArticleRequest.exec({
      requestVariables: {
        id: identifier,
      },
      onSuccess: (response) => {
        const article = response.data.article;
        setUpdateArticleForm({
          title: article.title,
          description: article.description,
        });
      },
      onFinally: () => {
        setLoading(false);
      },
    });
  }, []);

  const submit = () => {
    if (!updateArticleForm) {
      throw new Error("Update article form is undefined");
    }

    setLoading(true);
    updateArticleRequest.exec({
      data: {
        title: updateArticleForm.title,
        description: updateArticleForm.description,
      },
      requestVariables: {
        id: identifier,
      },
      onSuccess: (httpResponse) => {
        navigate(getArticleRoute(httpResponse.data.article.id));
      },
      onFinally: () => {
        setLoading(false);
      },
    });
  };

  if (!updateArticleForm) {
    return <Spin></Spin>;
  }

  return (
    <>
      <Spin delay={100} spinning={loading}>
        <Divider orientation="center">Update article</Divider>
        <Row>
          <Col span={24}>
            <Form
              name="submitForm"
              labelCol={{ span: 16 }}
              wrapperCol={{ span: 14 }}
              layout="horizontal"
              disabled={false}
              style={{ maxWidth: 1000 }}
              autoComplete="off"
              onFinish={() => submit()}
              initialValues={{
                title: updateArticleForm?.title,
                description: updateArticleForm?.description,
              }}
            >
              <Form.Item<UpdateArticleForm>
                name="title"
                label="Title"
                rules={[{ required: true, message: "Field cannot be empty!" }]}
              >
                <Input
                  maxLength={255}
                  onChange={(e) =>
                    setUpdateArticleForm({
                      title: e.target.value,
                      description: updateArticleForm?.description || null,
                    })
                  }
                />
              </Form.Item>

              <Form.Item<UpdateArticleForm>
                name="description"
                label="Description"
              >
                <TextArea
                  onChange={(e) => {
                    setUpdateArticleForm({
                      title: updateArticleForm?.title || "",
                      description: e.target.value,
                    });
                  }}
                ></TextArea>
              </Form.Item>

              <Form.Item wrapperCol={{ offset: 19, span: 14 }}>
                <Button type="primary" htmlType="submit">
                  Submit
                </Button>
              </Form.Item>
            </Form>
          </Col>
        </Row>
      </Spin>
    </>
  );
};

export default WithAuthorization(UpdateArticlePage);
