import { Form, Input, Button, Row, Col, Spin, Divider } from "antd";
import React from "react";
import { useHttpRequest } from "../../../../hook/useHttpRequestHook";
import WithAuthorization from "../../../../hoc/authorized/WithAuthorization";
import { useNavigate } from "react-router-dom";
import TextArea from "antd/es/input/TextArea";
import { CreateRoadMapRequest } from "../../service/request/CreateRoadMapRequest";
import { getRoadMapRoute } from "../../route/MathsRoadMapRouteGetter";

interface CreateRoadMapForm {
  title: string;
  description: string | null;
}

const CreateRoadMapPage: React.FC<{}> = () => {
  const [creatRoadMapForm, setCreateRoadMapForm] = React.useState<
    CreateRoadMapForm | undefined
  >(undefined);
  const [loading, setLoading] = React.useState<boolean>(false);
  const createRoadMapRequest: CreateRoadMapRequest =
    useHttpRequest(CreateRoadMapRequest);
  const navigate = useNavigate();

  const submit = () => {
    if (!creatRoadMapForm) {
      throw new Error("Create road-map form is undefined");
    }

    setLoading(true);
    createRoadMapRequest.exec({
      data: {
        title: creatRoadMapForm.title,
        description: creatRoadMapForm.description,
      },
      onSuccess: (httpResponse) => {
        navigate(getRoadMapRoute(httpResponse.data.id));
      },
      onFinally: () => {
        setLoading(false);
      },
    });
  };

  return (
    <>
      <Spin delay={100} spinning={loading}>
        {getCreateForm(
          submit,
          (value) =>
            setCreateRoadMapForm({
              title: value,
              description: creatRoadMapForm?.description || null,
            }),
          (value) =>
            setCreateRoadMapForm({
              description: value,
              title: creatRoadMapForm?.title || "",
            }),
        )}
      </Spin>
    </>
  );
};

function getCreateForm(
  onFinish: () => void,
  onChangeTitle: (a: string) => void,
  onChangeDescription: (a: string) => void,
): React.ReactElement {
  return (
    <>
      <Divider orientation="center">Create road-map</Divider>
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
            onFinish={onFinish}
          >
            <Form.Item<CreateRoadMapForm>
              name="title"
              label="Title"
              rules={[{ required: true, message: "Field cannot be empty!" }]}
            >
              <Input
                maxLength={255}
                onChange={(e) => onChangeTitle(e.target.value)}
              />
            </Form.Item>

            <Form.Item<CreateRoadMapForm>
              name="description"
              label="Description"
            >
              <TextArea
                onChange={(e) => onChangeDescription(e.target.value)}
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
    </>
  );
}

export default WithAuthorization(CreateRoadMapPage);
