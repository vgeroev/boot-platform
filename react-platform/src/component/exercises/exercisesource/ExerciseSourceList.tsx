import { Button, Col, Divider, Form, Input, Modal, Row, Spin } from "antd";
import axios from "axios";
import React from "react";
import { AuthState, useAuth } from "react-oidc-context";
import { useNavigate } from "react-router-dom";
import { getAuthorizedExercisesUrl } from ".";
import ExerciseSource, {
  ExerciseSourceDto,
  ExerciseSourceStats,
} from "./ExerciseSource";

interface ExerciseSourceListElement {
  stats: ExerciseSourceStats;
  exerciseSource: ExerciseSourceDto;
}

async function createExSource(auth: AuthState, values: any): Promise<void> {
  try {
    const token = auth.user?.access_token;
    if (!token) {
      throw new Error("Failed to get token");
    }

    await axios
      .post<any>(
        getAuthorizedExercisesUrl(`/exercise-source`),
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
const ExerciseSourceList: React.FC<{}> = () => {
  const [runRender, setRunRender] = React.useState(0);
  const [modalOpen, setModalOpen] = React.useState(false);
  const auth = useAuth();
  const [listElements, setListElements] = React.useState<
    Array<ExerciseSourceListElement> | undefined
  >(undefined);
  const [form] = Form.useForm();

  React.useEffect(() => {
    (async () => {
      try {
        const token = auth.user?.access_token;
        if (!token) {
          return;
        }

        await axios
          .get<any, Array<ExerciseSourceListElement>>(
            getAuthorizedExercisesUrl("/exercise-source/list"),
            {
              headers: {
                Authorization: `Bearer ${token}`,
              },
            },
          )
          .then((response: any) => {
            setListElements(response.data as Array<ExerciseSourceListElement>);
          });
      } catch (e) {
        console.error(e);
      }
    })();
  }, [runRender, auth]);

  if (!listElements) {
    return <Spin></Spin>;
  }

  const renderRunner = () => {
    setRunRender(runRender + 1);
  };
  return (
    <>
      <Divider orientation="center">Exercise sources</Divider>
      <Row>
        <Button type="primary" onClick={() => setModalOpen(true)}>
          Create exercise source
        </Button>

        <Modal
          title="Create exercise source"
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
              createExSource(auth, values).then(() => {
                renderRunner();
              });
            }}
          >
            <Form.Item name="name" label="Name" rules={[{ required: true }]}>
              <Input defaultValue="" />
            </Form.Item>
          </Form>
        </Modal>
      </Row>
      {listElements.map((element) => {
        return (
          <Row gutter={[16, 16]}>
            <Col span={9} />
            <Col span={6}>
              <ExerciseSource
                key={element.exerciseSource.id}
                exerciseSource={element.exerciseSource}
                stats={element.stats}
                renderParent={renderRunner}
              />
            </Col>
            <Col span={9} />
          </Row>
        );
      })}
    </>
  );
};

export default ExerciseSourceList;
