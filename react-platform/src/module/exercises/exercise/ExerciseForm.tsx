import "katex/dist/katex.min.css";
import { Button, Checkbox, Divider, Form, Input, Select } from "antd";
import TextArea from "antd/es/input/TextArea";
import React from "react";
import { SolutionStatus } from "../utils/BasicTypes";
import Latex from "react-latex-next";
import { Link } from "react-router-dom";
import { getExerciseListPath } from "../route/ExercisesRouteGetter";

export interface ExerciseFormProps {
  exerciseSourceId: number;
  problemName?: string;
  problem?: string;
  solutionStatus?: SolutionStatus;
  solution?: string;
  formDisabled?: boolean;
  onSubmit: (values: any) => void;
}

function escapeLatex(latex: string): string {
  return "\\[" + latex + "\\]";
}

const ExerciseForm: React.FC<ExerciseFormProps> = ({
  exerciseSourceId,
  problemName,
  problem,
  solutionStatus,
  solution,
  formDisabled,
  onSubmit,
}: ExerciseFormProps) => {
  const [componentDisabled, setComponentDisabled] = React.useState<boolean>(
    formDisabled === true,
  );
  const [realTimeProblem, setRealTimeProblem] = React.useState<string>(
    problem || "",
  );
  const [realTimeSolution, setRealTimeSolution] = React.useState<string>(
    solution || "",
  );

  return (
    <>
      <Link
        to={getExerciseListPath(exerciseSourceId)}
        style={{ fontSize: "15px" }}
      >
        Back to exercises{" "}
      </Link>

      <Divider>Exercise</Divider>

      <Checkbox
        checked={componentDisabled}
        onChange={(e) => setComponentDisabled(e.target.checked)}
      >
        Form disabled
      </Checkbox>

      <Form
        labelCol={{ span: 10 }}
        wrapperCol={{ span: 14 }}
        layout="horizontal"
        disabled={componentDisabled}
        style={{ maxWidth: 1200 }}
        onFinish={onSubmit}
      >
        <Form.Item name="problemName" label="Problem name">
          <Input defaultValue={problemName} />
        </Form.Item>

        <Form.Item name="solutionStatus" label="Solution status">
          <Select defaultValue={solutionStatus}>
            <Select.Option value="UNSOLVED">Unsolved</Select.Option>
            <Select.Option value="SOLVED">Solved</Select.Option>
            <Select.Option value="UNSURE">Unsure</Select.Option>
            <Select.Option value="PARTIALLY_SOLVED">
              Partially solved
            </Select.Option>
          </Select>
        </Form.Item>

        <Form.Item name="problem" label="Raw problem">
          <TextArea
            defaultValue={problem}
            onChange={(e) => setRealTimeProblem(e.target.value)}
            rows={10}
          ></TextArea>
        </Form.Item>
        <Form.Item name="realTimeProblem" label="Problem">
          <div style={{ fontSize: "20px" }}>
            <Latex>{escapeLatex(realTimeProblem)}</Latex>
          </div>
        </Form.Item>

        <Form.Item name="solution" label="Raw solution">
          <TextArea
            defaultValue={solution}
            onChange={(e) => setRealTimeSolution(e.target.value)}
            rows={10}
          />
        </Form.Item>
        <Form.Item name="realTimeSolution" label="Solution">
          <div style={{ fontSize: "20px" }}>
            <Latex>{escapeLatex(realTimeSolution)}</Latex>
          </div>
        </Form.Item>

        <Form.Item>
          <Button type="primary" htmlType="submit">
            Save
          </Button>
        </Form.Item>
      </Form>
    </>
  );
};

export default ExerciseForm;
