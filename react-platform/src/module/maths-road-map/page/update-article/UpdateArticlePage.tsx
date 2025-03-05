import {
  Form,
  Input,
  Button,
  Row,
  Col,
  Spin,
  Divider,
  Switch,
  FormInstance,
} from "antd";
import React from "react";
import { useHttpRequest } from "../../../../hook/useHttpRequestHook";
import WithAuthorization from "../../../../hoc/authorized/WithAuthorization";
import { useNavigate, useParams } from "react-router-dom";
import { getArticleRoute } from "../../route/MathsRoadMapRouteGetter";
import TextArea from "antd/es/input/TextArea";
import { UpdateArticleRequest } from "../../service/request/UpdateArticleRequest";
import { GetArticleRequest } from "../../service/request/GetArticleRequest";
import LatexEditorWithRender from "../../component/latex-editor-with-render/LatexEditorWithRender";
import { GetArticleLatexRequest } from "../../service/request/GetArticleLatexRequest";
import { UpdateArticleLatexRequest } from "../../service/request/UpdateArticleLatexRequest";
import UpdateArticleTagsForm from "./UpdateArticleTagsForm";

interface UpdateArticleForm {
  title?: string;
  description?: string | null;
  latex?: string;
  configuration?: string | null;
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
  const getArticleLatexRequest: GetArticleLatexRequest = useHttpRequest(
    GetArticleLatexRequest,
  );
  const updateArticleRequest: UpdateArticleRequest =
    useHttpRequest(UpdateArticleRequest);
  const updateArticleLatexRequest: UpdateArticleLatexRequest = useHttpRequest(
    UpdateArticleLatexRequest,
  );
  const navigate = useNavigate();
  const [showUpdateLatexForm, setShowUpdateLatexForm] =
    React.useState<boolean>(false);
  const [form] = Form.useForm();

  const articleGetter = () => {
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
  };
  const articleLatexGetter = () => {
    setLoading(true);
    getArticleLatexRequest.exec({
      requestVariables: {
        id: identifier,
      },
      onSuccess: (response) => {
        const articleLatex = response.data;
        setUpdateArticleForm({
          latex: articleLatex.latex,
          configuration: articleLatex.configuration,
        });
      },
      onFinally: () => {
        setLoading(false);
      },
    });
  };
  const switchUpdateForm = () => {
    setShowUpdateLatexForm(!showUpdateLatexForm);
  };

  React.useEffect(() => {
    if (showUpdateLatexForm) {
      articleLatexGetter();
    } else {
      articleGetter();
    }
  }, [showUpdateLatexForm]);

  React.useEffect(() => {
    form.setFieldsValue({
      title: updateArticleForm?.title || "",
      description: updateArticleForm?.description || "",
    });
  }, [form, updateArticleForm]);

  const submitArticleUpdate = () => {
    setLoading(true);
    updateArticleRequest.exec({
      data: {
        title: updateArticleForm?.title,
        description: updateArticleForm?.description,
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

  const submitArticleLatexUpdate = () => {
    setLoading(true);
    updateArticleLatexRequest.exec({
      data: {
        latex: updateArticleForm?.latex,
        configuration: updateArticleForm?.configuration,
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
      <Divider orientation="center">Update article</Divider>
      <Spin delay={100} spinning={loading}>
        <Switch onChange={switchUpdateForm} />
        {!showUpdateLatexForm &&
          getUpdateForm(
            identifier,
            form,
            submitArticleUpdate,
            (value) =>
              setUpdateArticleForm({
                title: value,
                description: updateArticleForm?.description || null,
              }),
            (value) =>
              setUpdateArticleForm({
                description: value,
                title: updateArticleForm?.title || "",
              }),
          )}
        {showUpdateLatexForm && (
          <div style={{ marginTop: "20px" }}>
            <Button
              style={{ marginBottom: "20px", marginLeft: "23px" }}
              type="primary"
              onClick={submitArticleLatexUpdate}
            >
              Submit
            </Button>
            <LatexEditorWithRender
              latexGetter={() => [
                updateArticleForm.latex || "",
                updateArticleForm.configuration || "",
              ]}
              onClear={() => {
                setUpdateArticleForm({ latex: "" });
              }}
              onConfigurationChange={(configuration) => {
                setUpdateArticleForm({
                  ...updateArticleForm,
                  configuration: configuration,
                });
              }}
              onLatexChange={(latex) => {
                setUpdateArticleForm({ ...updateArticleForm, latex: latex });
              }}
            />
          </div>
        )}
      </Spin>
    </>
  );
};

function getUpdateForm(
  articleId: number,
  form: FormInstance<any>,
  onFinish: () => void,
  onChangeTitle: (a: string) => void,
  onChangeDescription: (a: string) => void,
): React.ReactElement {
  return (
    <>
      <Row>
        <Col span={24}>
          <Form
            form={form}
            name="submitForm"
            labelCol={{ span: 16 }}
            wrapperCol={{ span: 14 }}
            layout="horizontal"
            disabled={false}
            style={{ maxWidth: 1000 }}
            autoComplete="off"
            onFinish={onFinish}
          >
            <Form.Item<UpdateArticleForm>
              name="title"
              label="Title"
              rules={[{ required: true, message: "Field cannot be empty!" }]}
            >
              <Input
                maxLength={255}
                onChange={(e) => onChangeTitle(e.target.value)}
              />
            </Form.Item>

            <Form.Item<UpdateArticleForm>
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
      <Row>
        <UpdateArticleTagsForm articleId={articleId} />
      </Row>
    </>
  );
}

export default WithAuthorization(UpdateArticlePage);
