import { Form, Input, Button, Row, Col, Spin, Divider, Modal } from "antd";
import React from "react";
import "prismjs/themes/prism-solarizedlight.css";
import { latexEditorService } from "../../service/latexeditor/LatexEditorService";
import { useHttpRequest } from "../../../../hook/useHttpRequestHook";
import {
  CreateArticleRequest,
  CreateArticleRequestData,
} from "../../service/request/CreateArticleRequest";
import WithAuthorization from "../../../../hoc/authorized/WithAuthorization";
import { useNavigate } from "react-router-dom";
import { getArticleRoute } from "../../route/MathsRoadMapRouteGetter";
import TextArea from "antd/es/input/TextArea";
import LatexEditorWithRender from "../../component/latex-editor-with-render/LatexEditorWithRender";

const getDefaultConfiguration = (): string => {
  return `\\Preamble{xhtml,mathjax} 
\\begin{document} 
  \\Css{body { color : green; }} 
\\EndPreamble`;
};

const getLatexTemplate = (): string => {
  return `\\documentclass{article}
\\title{My first LaTeX document}
\\author{Johann Georg Faust\\thanks{boot-platform}}
\\date{November 1988}
\\begin{document}
\\maketitle
Template

% This line here is a comment.
\\end{document}`;
};

interface TeX4ht {
  latex: string;
  configuration?: string;
}

interface CreateArticleForm {
  title?: string;
  description?: string;
}

const CreateArticlePage: React.FC<{}> = () => {
  const [createArticleForm, setCreateArticleForm] = React.useState<
    CreateArticleForm | undefined
  >(undefined);
  const [tex4ht, setTex4ht] = React.useState<TeX4ht>({
    latex: latexEditorService.getLatex() || getLatexTemplate(),
    configuration:
      latexEditorService.getConfiguration() || getDefaultConfiguration(),
  });
  const [loading, setLoading] = React.useState<boolean>(false);
  const createArticleRequest: CreateArticleRequest =
    useHttpRequest(CreateArticleRequest);
  const navigate = useNavigate();

  const submit = () => {
    if (!createArticleForm) {
      throw new Error("No createArticleForm");
    }
    if (!createArticleForm.title) {
      throw new Error("title should not be empty");
    }

    const requestData: CreateArticleRequestData = {
      title: createArticleForm.title.trim(),
      description: createArticleForm.description || null,
      latex: tex4ht.latex,
      configuration: tex4ht.configuration,
    };

    setLoading(true);
    createArticleRequest.exec({
      data: requestData,
      onSuccess: (httpResponse) => {
        navigate(getArticleRoute(httpResponse.data.article.id + ""));
      },
      handleModuleError: (httpResponse) => {
        switch (httpResponse.data.code) {
          case "invalid_latex_syntax":
            Modal.error({
              title: "Article creation error",
              content: httpResponse.data.parameters?.cmdOutput + "",
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
  };

  return (
    <>
      <Spin delay={100} spinning={loading}>
        <Divider orientation="center">Create article</Divider>
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
            >
              <Form.Item<CreateArticleForm>
                name="title"
                label="Title"
                rules={[{ required: true, message: "Field cannot be empty!" }]}
              >
                <Input
                  maxLength={255}
                  onChange={(e) =>
                    setCreateArticleForm({
                      ...createArticleForm,
                      title: e.target.value,
                    })
                  }
                />
              </Form.Item>

              <Form.Item<CreateArticleForm>
                name="description"
                label="Description"
              >
                <TextArea
                  onChange={(e) => {
                    setCreateArticleForm({
                      ...createArticleForm,
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

        <LatexEditorWithRender
          latexGetter={() => [tex4ht.latex, tex4ht.configuration || ""]}
          onClear={() => {
            latexEditorService.clearUserInput();
            setTex4ht({ latex: "" });
          }}
          onConfigurationChange={(configuration) => {
            setTex4ht({ ...tex4ht, configuration: configuration });
            latexEditorService.saveConfiguration(configuration);
          }}
          onLatexChange={(latex) => {
            setTex4ht({ ...tex4ht, latex: latex });
            latexEditorService.saveLatex(latex);
          }}
        />
      </Spin>
    </>
  );
};

export default WithAuthorization(CreateArticlePage);
