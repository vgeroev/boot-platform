import { Spin } from "antd";
import axios from "axios";
import React from "react";
import { useAuth } from "react-oidc-context";
import { useParams } from "react-router-dom";
import ExerciseForm from "./ExerciseForm";

export interface Excercise {
    id: number;
    problemName: string;
    problem: string;
    solutionStatus: "UNSOLVED" | "SOLVED" | "UNSURE";
    solution: string;
}
const Exercise: React.FC<{}> = () => {
    const { exerciseId } = useParams();
    const [excercise, setExercise] = React.useState<Excercise | undefined>(
        undefined,
    );
    const [submitTrigger, setSubmitTrigger] = React.useState<boolean>(false);
    const auth = useAuth();

    // React.useEffect(() => {
    //     (async () => {
    //         try {
    //             const token = auth.user?.access_token;
    //             if (!token) {
    //                 return;
    //             }
    //
    //             await axios
    //                 .get<any, Excercise>(
    //                     getAuthorizedExercisesUrl(
    //                         `/exercise-source/exercise/${exerciseId}`,
    //                     ),
    //                     {
    //                         headers: {
    //                             Authorization: `Bearer ${token}`,
    //                         },
    //                     },
    //                 )
    //                 .then((response: any) => {
    //                     setExercise(response.data as Excercise);
    //                 });
    //         } catch (e) {
    //             console.error(e);
    //         }
    //     })();
    // }, [submitTrigger, auth, exerciseId]);
    //
    if (!excercise) {
        return <Spin></Spin>;
    }

    // const onSubmit = (values: any) => {
    //     (async () => {
    //         try {
    //             const token = auth.user?.access_token;
    //             if (!token) {
    //                 return;
    //             }
    //
    //             await axios
    //                 .patch(
    //                     getAuthorizedExercisesUrl(
    //                         `/exercise-source/exercise/${exerciseId}`,
    //                     ),
    //                     {
    //                         problemName: values.problemName,
    //                         problem: values.problem,
    //                         solutionStatus: values.solutionStatus,
    //                         solution: values.solution,
    //                     },
    //                     {
    //                         headers: {
    //                             Authorization: `Bearer ${token}`,
    //                         },
    //                     },
    //                 )
    //                 .then((response: any) => {
    //                     setSubmitTrigger(!submitTrigger);
    //                 });
    //         } catch (e) {
    //             console.error(e);
    //         }
    //     })();
    // };

    return (
        <ExerciseForm
            formDisabled={true}
            problemName={excercise?.problemName}
            problem={excercise?.problem}
            solutionStatus={excercise?.solutionStatus}
            solution={excercise?.solution}
            // onSubmit={onSubmit}
            onSubmit={() => { }}
        />
    );
};

export default Exercise;
