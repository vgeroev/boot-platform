import { Affix, Button, Col, Row, Spin } from "antd";
import React from "react";
import Iframe from "react-iframe";
import { useHttpRequest } from "../../../../hook/useHttpRequestHook";
import { PreviewArticleRequest } from "../../service/request/PreviewArticleRequest";
import { LatexEditor } from "../latex-editor/LatexEditor";
import "./styles.css";

interface RenderResult {
  url?: string;
  errorMsg?: string;
}

interface Props {
  onLatexChange?: (latex: string) => void;
  onConfigurationChange?: (configuration: string) => void;
  onClear?: () => void;
  latexGetter: () => [string, string | null];
}

const LatexEditorWithRender: React.FC<Props> = ({
  onLatexChange,
  onConfigurationChange,
  onClear,
  latexGetter,
}) => {
  const [loading, setLoading] = React.useState<boolean>(false);
  const [renderResult, setRenderResult] = React.useState<
    RenderResult | undefined
  >(undefined);
  // For iframe reloading
  const [checksum, setChecksum] = React.useState<number>(0);
  const previewArticleRequest: PreviewArticleRequest = useHttpRequest(
    PreviewArticleRequest,
  );

  const render = (latex: string, configuration: string | null) => {
    setLoading(true);

    previewArticleRequest.exec({
      data: {
        latex: latex,
        configuration: configuration || undefined,
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

  const [latex, configuration] = latexGetter();

  return (
    <Spin spinning={loading}>
      <Row justify="start" style={{ marginLeft: "23px", marginBottom: "20px" }}>
        <Col span={1}>
          <Button
            onClick={() => {
              if (onClear) {
                onClear();
              }
            }}
          >
            Clear
          </Button>
        </Col>
        <Col span={1}>
          <Button
            onClick={() => {
              render(latex, configuration);
            }}
          >
            Render
          </Button>
        </Col>
      </Row>

      <Row justify="space-evenly">
        <Col span={11}>
          <Row>
            <LatexEditor
              withHighlight
              value={configuration || ""}
              onValueChange={(code) => {
                if (onConfigurationChange) {
                  onConfigurationChange(code);
                }
              }}
            />
          </Row>
          <Row style={{ paddingTop: 10 }}>
            <LatexEditor
              withHighlight
              value={latex}
              onValueChange={(code) => {
                if (onLatexChange) {
                  onLatexChange(code);
                }
              }}
            />
          </Row>
        </Col>
        <Col span={12}>
          <Affix offsetTop={75}>{loadIframe(checksum, renderResult)}</Affix>
        </Col>
      </Row>
    </Spin>
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

export default LatexEditorWithRender;
