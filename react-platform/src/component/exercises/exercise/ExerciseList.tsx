import { Button, Col, Divider, Row, Spin } from "antd";
import axios from "axios";
import React from "react";
import { AuthState, useAuth } from "react-oidc-context";
import { useNavigate, useParams } from "react-router-dom";
import { getAuthorizedExercisesUrl } from "../exercisesource";
import ExerciseCard from "./ExerciseCard";

interface PaginatedDto<T> {
  result: Array<T>;
  totalCount: number;
  page: number;
  pageSize: number | null;
}

interface ExcerciseListElement {
  id: number;
  problemName: string;
  problem: string;
  solutionStatus: "UNSOLVED" | "SOLVED" | "UNSURE";
}

interface ExercisePaginated extends PaginatedDto<ExcerciseListElement> {}

const ExerciseList: React.FC<{}> = () => {
  const { exerciseSourceId } = useParams();
  const navigate = useNavigate();
  const [excercises, setExcercises] = React.useState<
    ExercisePaginated | undefined
  >(undefined);
  const auth: AuthState = useAuth();
  React.useEffect(() => {
    (async () => {
      try {
        const token = auth.user?.access_token;
        if (!token) {
          return;
        }

        await axios
          .get<any, ExercisePaginated>(
            getAuthorizedExercisesUrl(
              `/exercise-source/${exerciseSourceId}/exercise/list`,
            ),
            {
              headers: {
                Authorization: `Bearer ${token}`,
              },
            },
          )
          .then((response: any) => {
            setExcercises(response.data as ExercisePaginated);
          });
      } catch (e) {
        console.error(e);
      }
    })();
  }, [exerciseSourceId, auth]);

  if (!excercises) {
    return <Spin></Spin>;
  }

  return (
    <>
      <Divider orientation="center">Exercises</Divider>
      <Button
        type="primary"
        onClick={() => navigate(`/exercises/${exerciseSourceId}/new`)}
      >
        Create exercise
      </Button>
      {excercises.result.map((element) => {
        return (
          <Row gutter={[16, 16]}>
            <Col span={9} />
            <Col span={6}>
              <ExerciseCard
                key={element.id}
                id={element.id}
                problemName={element.problemName}
                problem={element.problem}
                solutionStatus={element.solutionStatus}
              />
            </Col>
            <Col span={9} />
          </Row>
        );
      })}
    </>
  );
};

export default ExerciseList;
