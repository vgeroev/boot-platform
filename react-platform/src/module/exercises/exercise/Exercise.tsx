import axios from "axios";
import React from "react";
import { AuthState, useAuth } from "react-oidc-context";
import { useParams } from "react-router-dom";
import Spinner from "../../../component/spinner/Spinner";
import { HttpRequest } from "../../../utils/HttpUtils";
import { SolutionStatus } from "../utils/BasicTypes";
import {
  getExerciseHttpRequest,
  updateExerciseHttpRequest,
} from "../utils/ExercisesHttpUtils";
import ExerciseForm from "./ExerciseForm";

interface ExerciseDto {
  id: number;
  createdAt: string;
  updatedAt: string;
  problemName: string;
  problem: string;
  solutionStatus: SolutionStatus;
  solution: string;
  exerciseSourceId: number;
}

interface UpdateForm {
  problemName: string;
  problem: string;
  solutionStatus: SolutionStatus;
  solution: string;
}

async function getDomainObject(
  auth: AuthState,
  id: number,
  onSuccess: (result: ExerciseDto) => void,
  onError: (e: Error) => void,
): Promise<void> {
  try {
    const token = auth.user?.access_token;
    if (!token) {
      throw new Error("Failed to get token");
    }

    const httpRequest: HttpRequest = getExerciseHttpRequest(id);

    await axios
      .request<any>({
        url: httpRequest.url,
        method: httpRequest.method,
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then((response: any) => {
        onSuccess(response.data);
      })
      .catch((e: Error) => onError(e));
  } catch (e) {
    console.error(e);
  }
}

async function update(
  auth: AuthState,
  id: number,
  updateForm: UpdateForm,
  onSuccess: (result: ExerciseDto) => void,
  onError: (e: Error) => void,
): Promise<void> {
  try {
    const token = auth.user?.access_token;
    if (!token) {
      throw new Error("Failed to get token");
    }

    const httpRequest: HttpRequest = updateExerciseHttpRequest(id);

    await axios
      .request<any>({
        url: httpRequest.url,
        method: httpRequest.method,
        headers: {
          Authorization: `Bearer ${token}`,
        },
        data: {
          problemName: updateForm.problemName,
          problem: updateForm.problem,
          solutionStatus: updateForm.solutionStatus,
          solution: updateForm.solution,
        },
      })
      .then((response: any) => {
        onSuccess(response.data);
      })
      .catch((e: Error) => onError(e));
  } catch (e: any) {
    console.error(e);
  }
}
const Exercise: React.FC<{}> = () => {
  const { id } = useParams();
  const exId = Number(id);
  const auth: AuthState = useAuth();
  const [loading, setLoading] = React.useState<boolean>(false);
  const [exercise, setExercise] = React.useState<ExerciseDto | undefined>(
    undefined,
  );

  React.useEffect(() => {
    getDomainObject(
      auth,
      exId,
      (result: ExerciseDto) => setExercise(result),
      (e: Error) => console.error(e),
    );
  }, [exId, auth]);

  if (!exercise || loading) {
    return <Spinner />;
  }

  const onSubmit = (values: any) => {
    setLoading(true);
    update(
      auth,
      exId,
      {
        problemName: values.problemName,
        problem: values.problem,
        solutionStatus: values.solutionStatus,
        solution: values.solution,
      },
      (result: ExerciseDto) => {
        setExercise(result);
      },
      (e: Error) => console.error(e),
    ).finally(() => {
      setLoading(false);
    });
  };

  return (
    <>
      <ExerciseForm
        exerciseSourceId={exercise.exerciseSourceId}
        problemName={exercise.problemName}
        problem={exercise.problem}
        solutionStatus={exercise.solutionStatus}
        solution={exercise.solution}
        onSubmit={onSubmit}
        formDisabled
      />
    </>
  );
};

export default Exercise;
