import axios from "axios";
import React from "react";
import { useAuth } from "react-oidc-context";
import { useNavigate, useParams } from "react-router-dom";
import { getAuthorizedExercisesUrl } from "../exercisesource";
import ExerciseForm from "./ExerciseForm";

const ExerciseNew: React.FC<{}> = () => {
  const { exerciseSourceId } = useParams();
  const navigate = useNavigate();
  const auth = useAuth();

  const onSubmit = (values: any) => {
    (async () => {
      try {
        const token = auth.user?.access_token;
        if (!token) {
          return;
        }

        await axios
          .post(
            getAuthorizedExercisesUrl(
              `/exercise-source/${exerciseSourceId}/exercise`,
            ),
            {
              problemName: values.problemName,
              problem: values.problem,
              solutionStatus: values.solutionStatus,
              solution: values.solution,
            },
            {
              headers: {
                Authorization: `Bearer ${token}`,
              },
            },
          )
          .then((response: any) => {
            navigate(`/exercises/${exerciseSourceId}/list`);
          });
      } catch (e) {
        console.error(e);
      }
    })();
  };

  return <ExerciseForm onSubmit={onSubmit} />;
};

export default ExerciseNew;
