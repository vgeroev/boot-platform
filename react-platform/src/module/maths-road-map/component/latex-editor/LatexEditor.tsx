import Prism, { highlight } from "prismjs";
import { CSSProperties } from "react";
import Editor from "react-simple-code-editor";
import "./styles.css";
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

export interface LatexEditorProps {
    value: string;
    withHighlight?: boolean;
    onValueChange?: (value: string) => void;
    style?: CSSProperties;
}

export const LatexEditor: React.FC<LatexEditorProps> = ({
    value,
    withHighlight = true,
    onValueChange,
    style,
}: LatexEditorProps) => {
    return (
        <Editor
            className="editor"
            textareaId="codeArea"
            value={value}
            onValueChange={onValueChange ? onValueChange : () => { }}
            highlight={(code) => {
                if (withHighlight) {
                    return highlightWithLineNumbers(code).code;
                }
                return getCodeWithLineNumbers(code);
            }}
            padding={15}
            style={
                style
                    ? style
                    : {
                        fontFamily: '"Fira code", "Fira Mono", monospace',
                        fontSize: 16,
                        color: "black",
                    }
            }
        />
    );
};
