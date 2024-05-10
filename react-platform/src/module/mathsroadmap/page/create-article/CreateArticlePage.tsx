import {
  Form,
  Input,
  Select,
  Button,
  Row,
  Col,
  Spin,
  Divider,
  Affix,
} from "antd";
import React from "react";
import Editor from "react-simple-code-editor";
import Forbidden from "../../../../component/forbidden/Forbidden";
import Prism, { highlight } from "prismjs";
import "prismjs/themes/prism-solarizedlight.css";
import Iframe from "react-iframe";
import "./styles.css";
import { latexEditorService } from "../../service/latexeditor/LatexEditorService";
import { PreviewArticleRequest } from "../../service/request/PreviewArticleRequest";
import { useHttpRequest } from "../../../../hook/useHttpRequestHook";
require("prismjs/components/prism-latex.js");

interface HighlightedLatex {
  linesCount: number;
  code: string;
}

const highlightWithLineNumbers = (code: string): HighlightedLatex => {
  const highlighted = highlight(code, Prism.languages.latex, "latex");
  const modifiedLines: string[] = addLineNumbers(highlighted);
  return {
    linesCount: modifiedLines.length,
    code: modifiedLines.join("\n"),
  };
};

const getCodeWithLineNumbers = (code: string): string => {
  return addLineNumbers(code).join("\n");
};

const addLineNumbers = (code: string): Array<string> => {
  const modifiedLines = [];
  let i = 1;
  for (let line of code.split("\n")) {
    modifiedLines.push(`<span class='editorLineNumber'>${i}</span>${line}`);
    i++;
  }
  return modifiedLines;
};

const getDefaultConfiguration = (): string => {
  return `\\Preamble{xhtml,mathjax} 
\\begin{document} 
  \\Css{body { color : green; }} 
\\EndPreamble`;
};

interface RenderResult {
  url?: string;
  errorMsg?: string;
}

interface TeX4ht {
  latex: string;
  configuration?: string;
}

const CreateArticlePage: React.FC<{}> = () => {
  const [tex4ht, setTex4ht] = React.useState<TeX4ht>({
    latex: latexEditorService.getLatex() || "",
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

  const runRender = () => {
    setLoading(true);

    previewArticleRequest.exec({
      data: {
        latex: tex4ht.latex,
        configuration: tex4ht.configuration,
      },
      onSuccess: (httpResponse) => {
        setRenderResult({ url: httpResponse.data?.articleURL });
      },
      onModuleError: (httpResponse) => {
        setRenderResult({
          errorMsg: httpResponse.data?.parameters?.cmdOutput + "",
        });
      },
      onFinally: () => {
        setChecksum(checksum + 1);
        setLoading(false);
      },
    });
  };

  // React.useEffect(() => {
  //   const handleKeyPress = (event: any) => {
  //     console.log(event);
  //     if (event.altKey === true && event.keyCode === 13) {
  //       runRender();
  //     }
  //   };
  //   document.addEventListener("keydown", handleKeyPress, true);
  //   return () => {
  //     document.removeEventListener("keydown", handleKeyPress);
  //   };
  // }, [runRender]);

  // if (!auth?.isAuthenticated) {
  //   return <Forbidden />;
  // }

  return (
    <>
      <Spin delay={100} spinning={loading}>
        <Divider orientation="center">Create article</Divider>
        <Row>
          <Col span={24}>
            <Form
              labelCol={{ span: 16 }}
              wrapperCol={{ span: 14 }}
              layout="horizontal"
              disabled={false}
              style={{ maxWidth: 1000 }}
            >
              <Form.Item label="Title">
                <Input maxLength={255} />
              </Form.Item>
              <Form.Item label="Abstraction level">
                <Select>
                  <Select.Option value="LOW">
                    <div style={{ color: "#808080", fontWeight: 100 }}>Low</div>
                  </Select.Option>
                  <Select.Option value="MEDIUM">
                    <div style={{ color: "#000080", fontWeight: 100 }}>
                      Medium
                    </div>
                  </Select.Option>
                  <Select.Option value="SUPREME">
                    <div style={{ color: "#DAA520", fontWeight: 100 }}>
                      Supreme
                    </div>
                  </Select.Option>
                </Select>
              </Form.Item>
            </Form>
          </Col>
        </Row>

        <Row justify="space-between" style={{ padding: "10px" }}>
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
            <Button type="primary" onClick={(e) => runRender()}>
              Render
            </Button>
          </Col>
          <Col span={1}>
            <Button>Submit</Button>
          </Col>
        </Row>

        <Row justify={"space-evenly"}>
          <Col span={11}>
            <Row>
              <Editor
                className="editor"
                textareaId="codeArea"
                value={tex4ht.configuration || ""}
                onValueChange={(code) => {
                  setTex4ht({ ...tex4ht, configuration: code });
                  latexEditorService.saveConfiguration(code);
                }}
                highlight={(code) => {
                  return highlightWithLineNumbers(code).code;
                }}
                padding={15}
                style={{
                  fontFamily: '"Fira code", "Fira Mono", monospace',
                  fontSize: 16,
                  color: "black",
                }}
              />
            </Row>
            <Row>
              <Editor
                className="editor"
                textareaId="codeArea"
                value={tex4ht.latex}
                onValueChange={(code) => {
                  setTex4ht({ ...tex4ht, latex: code });
                  latexEditorService.saveLatex(code);
                }}
                highlight={(code) => {
                  return highlightWithLineNumbers(code).code;
                }}
                padding={15}
                style={{
                  fontFamily: '"Fira code", "Fira Mono", monospace',
                  fontSize: 16,
                  color: "black",
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
  if (renderResult.errorMsg) {
    return (
      <Editor
        className="editor"
        textareaId="codeArea"
        value={getCodeWithLineNumbers(renderResult.errorMsg.trim())}
        onValueChange={() => {}}
        highlight={(code) => {
          return code;
        }}
        padding={15}
        style={{
          fontFamily: '"Fira code", "Fira Mono", monospace',
          fontSize: 16,
          color: "black",
          height: window.screen.height - 209 + "px",
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
      height={window.screen.height - 209 + "px"}
      width="100%"
    />
  );
}

export default CreateArticlePage;
