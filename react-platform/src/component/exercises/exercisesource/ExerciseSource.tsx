import {
  CheckCircleTwoTone,
  EyeTwoTone,
  SyncOutlined,
} from "@ant-design/icons";
import { Button, Card, Form, Input, Modal, Row } from "antd";
import axios from "axios";
import React from "react";
import { AuthState, useAuth } from "react-oidc-context";
import { useNavigate } from "react-router-dom";
import { getAuthorizedExercisesUrl } from ".";

export interface ExerciseSourceDto {
  id: number;
  name: string;
  ownerId: string;
}

export interface ExerciseSolutionStats {
  UNSOLVED: number;
  SOLVED: number;
  UNSURE: number;
}

export interface ExerciseSourceStats {
  solutionStatusesStats: ExerciseSolutionStats;
}

interface Props {
  exerciseSource: ExerciseSourceDto;
  stats: ExerciseSourceStats;
  renderParent: () => void;
}

function getExercisesCount(
  solutionStats: ExerciseSolutionStats | undefined,
): number {
  if (!solutionStats) {
    return 0;
  }

  let solutionSum: number = 0;
  if (solutionStats.UNSOLVED) {
    solutionSum += solutionStats.UNSOLVED;
  }
  if (solutionStats.SOLVED) {
    solutionSum += solutionStats.SOLVED;
  }
  if (solutionStats.UNSURE) {
    solutionSum += solutionStats.UNSURE;
  }
  return solutionSum;
}

async function updateExSourceProfile(
  auth: AuthState,
  id: number,
  values: any,
): Promise<void> {
  try {
    const token = auth.user?.access_token;
    if (!token) {
      throw new Error("Failed to get token");
    }

    await axios
      .patch<any>(
        getAuthorizedExercisesUrl(`/exercise-source/${id}`),
        {
          name: values.name,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        },
      )
      .then((response: any) => {});
  } catch (e) {
    console.error(e);
  }
}

const ExerciseSource: React.FC<Props> = ({
  exerciseSource,
  stats,
  renderParent,
}: Props) => {
  const auth = useAuth();
  const navigate = useNavigate();
  const [modalOpen, setModalOpen] = React.useState(false);
  const [form] = Form.useForm();

  const solutionStats: ExerciseSolutionStats | undefined =
    stats?.solutionStatusesStats;

  return (
    <Card
      title={exerciseSource.name}
      style={{ width: "100%" }}
      hoverable
      onClick={() => navigate(`/exercises/${exerciseSource.id}/list`)}
    >
      <Button type="primary" onClick={() => setModalOpen(true)}>
        Open profile
      </Button>

      <Modal
        title="Exercise source profile"
        open={modalOpen}
        onOk={() => {
          form.submit();
          setModalOpen(false);
        }}
        onCancel={() => {
          setModalOpen(false);
          form.resetFields();
        }}
      >
        <Form
          form={form}
          onFinish={(values) => {
            updateExSourceProfile(auth, exerciseSource.id, values).then(() => {
              renderParent();
            });
          }}
        >
          <Form.Item name="name" label="Name" rules={[{ required: true }]}>
            <Input defaultValue={exerciseSource.name} />
          </Form.Item>
        </Form>
      </Modal>

      {solutionStats?.SOLVED && (
        <Row>
          <CheckCircleTwoTone twoToneColor="#52c41a" />
          <div style={{ marginLeft: "10px" }}>{solutionStats.SOLVED}</div>
        </Row>
      )}

      {solutionStats?.UNSOLVED && (
        <Row>
          <SyncOutlined spin />
          <div style={{ marginLeft: "10px" }}>{solutionStats?.UNSOLVED}</div>
        </Row>
      )}

      {solutionStats?.UNSURE && (
        <Row>
          <EyeTwoTone />
          <div style={{ marginLeft: "10px" }}>{solutionStats?.UNSURE}</div>
        </Row>
      )}
      <Row>
        <div>Exercises count: {getExercisesCount(solutionStats)}</div>
      </Row>
    </Card>
  );
};

export default ExerciseSource;
