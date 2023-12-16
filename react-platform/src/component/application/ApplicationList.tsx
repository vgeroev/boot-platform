import { Divider, Col, Row } from "antd";
import { useNavigate } from "react-router-dom";
import { EXERCISE_SOURCE_LIST_PATH } from "../../module/exercises/route/ExcercisesRouteGetter";
import { getMatrix } from "../../utils/GridUtils";
import Application from "./Application";

interface ApplicationDto {
  id: number;
  uuid: string;
  name: string;
  desc: string;
}

const EXERCISES_UUID = "exercises_uuid";

const applications: Array<ApplicationDto> = [
  {
    id: 1,
    uuid: EXERCISES_UUID,
    name: "Exercises",
    desc: "Your exercise solvings",
  },
];

function getApplicationPath(uuid: string): string {
  switch (uuid) {
    case EXERCISES_UUID: {
      return EXERCISE_SOURCE_LIST_PATH;
    }
  }

  throw new Error("Unknown module uuid: " + uuid);
}

const ApplicationList: React.FC<{}> = () => {
  const navigate = useNavigate();

  const span: number = 4;
  const elementsPerRow: number = 24 / span;
  const cRow = applications.length / elementsPerRow;

  const applicationMatrix: Array<Array<ApplicationDto>> = getMatrix(
    applications,
    cRow,
    elementsPerRow,
  );

  return (
    <>
      <Divider orientation="center">Applications</Divider>
      {applicationMatrix.map((row, i) => {
        return (
          <Row key={i} gutter={16} style={{ padding: "8px 0" }}>
            {row.map((element) => {
              return (
                <Col
                  key={element.id}
                  className="gutter-row"
                  span={span}
                  style={{ padding: "0px 10px" }}
                >
                  <Application
                    key={element.id}
                    name={element.name}
                    desc={element.desc}
                    onClick={() => navigate(getApplicationPath(element.uuid))}
                  />
                </Col>
              );
            })}
          </Row>
        );
      })}
    </>
  );
};

export default ApplicationList;
