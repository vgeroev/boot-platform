import "katex/dist/katex.min.css";
import { Button, Checkbox, Form, Input, Modal, Select } from "antd";
import TextArea from "antd/es/input/TextArea";
import React from "react";
import Latex from "react-latex";

export interface ExerciseFormProps {
  problemName?: string;
  problem?: string;
  solutionStatus?: "UNSOLVED" | "SOLVED" | "UNSURE";
  solution?: string;
  formDisabled?: boolean;
  onSubmit: (values: any) => void;
}

const ExerciseForm: React.FC<ExerciseFormProps> = ({
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

  const [modalOpen, setModalOpen] = React.useState(false);

  return (
    <>
      <Checkbox
        checked={componentDisabled}
        onChange={(e) => setComponentDisabled(e.target.checked)}
      >
        Form disabled
      </Checkbox>

      <Button type="primary" onClick={() => setModalOpen(true)}>
        Open solution in LaTeX
      </Button>
      <Modal
        title="Solution"
        open={modalOpen}
        onOk={() => {
          setModalOpen(false);
        }}
        onCancel={() => {
          setModalOpen(false);
        }}
      >
        {solution && (
          <div style={{ fontSize: "25px" }}>
            <Latex>{"$$" + solution + "$$"}</Latex>
          </div>
        )}
      </Modal>

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
        <Form.Item name="problem" label="Problem">
          <TextArea defaultValue={problem} rows={4}></TextArea>
        </Form.Item>
        <Form.Item name="solutionStatus" label="Solution status">
          <Select defaultValue={solutionStatus}>
            <Select.Option value="UNSOLVED">Unsolved</Select.Option>
            <Select.Option value="SOLVED">Solved</Select.Option>
            <Select.Option value="UNSURE">Unsure</Select.Option>
          </Select>
        </Form.Item>
        <Form.Item name="solution" label="Raw solution">
          <TextArea defaultValue={solution} />
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
