import axios from "axios";
import React from "react";
import { AuthState, useAuth } from "react-oidc-context";
import { useNavigate, useParams } from "react-router-dom";
import Spinner from "../../../component/spinner/Spinner";
import { HttpRequest } from "../../../utils/HttpUtils";
import { getExercisePath } from "../route/ExercisesRouteGetter";
import { SolutionStatus } from "../utils/BasicTypes";
import { createExerciseHttpRequest } from "../utils/ExercisesHttpUtils";
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

interface CreateForm {
  problemName: string;
  problem: string;
  solutionStatus: SolutionStatus;
  solution: string;
}

async function create(
  auth: AuthState,
  exerciseSourceId: number,
  createForm: CreateForm,
  onSuccess: (result: ExerciseDto) => void,
  onError: (e: Error) => void,
): Promise<void> {
  try {
    const token = auth.user?.access_token;
    if (!token) {
      throw new Error("Failed to get token");
    }

    const httpRequest: HttpRequest =
      createExerciseHttpRequest(exerciseSourceId);

    await axios
      .request<any>({
        url: httpRequest.url,
        method: httpRequest.method,
        headers: {
          Authorization: `Bearer ${token}`,
        },
        data: {
          problemName: createForm.problemName,
          problem: createForm.problem,
          solutionStatus: createForm.solutionStatus,
          solution: createForm.solution,
        },
      })
      .then((response: any) => {
        onSuccess(response.data);
      })
      .catch(onError);
  } catch (e: any) {
    console.error(e);
  }
}
const ExerciseNew: React.FC<{}> = () => {
  const { exerciseSourceId } = useParams();
  const exSourceId = Number(exerciseSourceId);
  const auth: AuthState = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = React.useState<boolean>(false);

  if (loading) {
    return <Spinner />;
  }

  const onSubmit = (values: any) => {
    setLoading(true);
    create(
      auth,
      exSourceId,
      {
        problemName: values.problemName,
        problem: values.problem,
        solutionStatus: values.solutionStatus,
        solution: values.solution,
      },
      (result: ExerciseDto) => {
        navigate(getExercisePath(result.id));
      },
      (e: any) => {
        // Not working :(
        // const errorCode = e.response?.data?.moduleError?.code;
        // console.log(e);
        // if (errorCode === "not_unique_domain_object") {
        //   console.log(errorCode);
        //   messageApi.error({ content: "Not unique problem name" });
        // } else {
        //   messageApi.error({ content: "Unknown error" });
        // }
      },
    ).finally(() => {
      setLoading(false);
    });
  };

  return (
    <>
      <ExerciseForm exerciseSourceId={exSourceId} onSubmit={onSubmit} />
    </>
  );
};

export default ExerciseNew;
