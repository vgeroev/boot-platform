import { Form, Input, Button, Row, Col, Spin, Divider, Affix } from "antd";
import React from "react";
import "prismjs/themes/prism-solarizedlight.css";
import Iframe from "react-iframe";
import "./styles.css";
import { latexEditorService } from "../../service/latexeditor/LatexEditorService";
import { PreviewArticleRequest } from "../../service/request/PreviewArticleRequest";
import { useHttpRequest } from "../../../../hook/useHttpRequestHook";
import { LatexEditor } from "../../component/latexeditor/LatexEditor";
import {
  CreateArticleRequest,
  CreateArticleRequestData,
} from "../../service/request/CreateArticleRequest";
import WithAuthorization from "../../../../hoc/authorized/WithAuthorization";
import { useNavigate } from "react-router-dom";
import { getArticleRoute } from "../../route/MathsRoadMapRouteGetter";
import TextArea from "antd/es/input/TextArea";

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
interface RenderResult {
  url?: string;
  errorMsg?: string;
}

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
  const [renderResult, setRenderResult] = React.useState<
    RenderResult | undefined
  >(undefined);
  // For iframe reloading
  const [checksum, setChecksum] = React.useState<number>(0);
  const [loading, setLoading] = React.useState<boolean>(false);
  const previewArticleRequest: PreviewArticleRequest = useHttpRequest(
    PreviewArticleRequest,
  );
  const createArticleRequest: CreateArticleRequest =
    useHttpRequest(CreateArticleRequest);
  const navigate = useNavigate();

  const render = () => {
    setLoading(true);

    previewArticleRequest.exec({
      data: {
        latex: tex4ht.latex,
        configuration: tex4ht.configuration,
      },
      onSuccess: (httpResponse) => {
        setRenderResult({ url: httpResponse.data?.articleURL });
      },
      handleModuleError: (httpResponse) => {
        switch (httpResponse.data.code) {
          case "invalid_latex_syntax":
            setRenderResult({
              errorMsg: httpResponse.data.parameters?.cmdOutput + "",
            });
            return true;
          default:
            return false;
        }
      },
      onFinally: () => {
        setChecksum(checksum + 1);
        setLoading(false);
      },
    });
  };

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
            setRenderResult({
              errorMsg: httpResponse.data.parameters?.cmdOutput + "",
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

        <Row
          justify="start"
          style={{ marginLeft: "23px", marginBottom: "20px" }}
        >
          <Col span={1}>
            <Button
              onClick={() => {
                latexEditorService.clearUserInput();
                setTex4ht({ latex: "" });
              }}
            >
              Clear
            </Button>
          </Col>
          <Col span={1}>
            <Button onClick={() => render()}>Render</Button>
          </Col>
        </Row>

        <Row justify="space-evenly">
          <Col span={11}>
            <Row>
              <LatexEditor
                withHighlight
                value={tex4ht.configuration || ""}
                onValueChange={(code) => {
                  setTex4ht({ ...tex4ht, configuration: code });
                  latexEditorService.saveConfiguration(code);
                }}
              />
            </Row>
            <Row style={{ paddingTop: 10 }}>
              <LatexEditor
                withHighlight
                value={tex4ht.latex}
                onValueChange={(code) => {
                  setTex4ht({ ...tex4ht, latex: code });
                  latexEditorService.saveLatex(code);
                }}
              />
            </Row>
          </Col>
          <Col span={12}>
            <Affix offsetTop={75}>{loadIframe(checksum, renderResult)}</Affix>
          </Col>
        </Row>
      </Spin>
    </>
  );
};

function loadIframe(
  checksum: number,
  renderResult: RenderResult | undefined,
): React.ReactNode {
  if (!renderResult) {
    return <div></div>;
  }

  const heightPx: number = window.screen.height - 209;
  if (renderResult.errorMsg) {
    return (
      <LatexEditor
        value={renderResult.errorMsg.trim()}
        style={{
          fontFamily: '"Fira code", "Fira Mono", monospace',
          fontSize: 16,
          color: "black",
          height: heightPx + "px",
          overflow: "scroll",
        }}
      />
    );
  }

  return (
    <Iframe
      className="previewArticle"
      allowFullScreen
      key={checksum + ""}
      url={renderResult.url || ""}
      height={heightPx + "px"}
      width="100%"
    />
  );
}

export default WithAuthorization(CreateArticlePage);
