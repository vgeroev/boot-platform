import { Card, Row } from "antd";
import React from "react";
import Latex from "react-latex";
import { useNavigate } from "react-router-dom";

type SolutionStatus = "UNSOLVED" | "SOLVED" | "UNSURE";

interface Props {
  id: number;
  problemName: string;
  problem: string;
  solutionStatus: SolutionStatus;
}

function getColor(solutionStatus: SolutionStatus): string {
  switch (solutionStatus) {
    case "SOLVED":
      return "#90EE90";
    case "UNSOLVED":
      return "#F08080";
    case "UNSURE":
      return "#87CEFA";
  }
}

const ExerciseCard: React.FC<Props> = ({
  id,
  problemName,
  problem,
  solutionStatus,
}: Props) => {
  const navigate = useNavigate();
  const onClick = () => {
    navigate(`/exercises/${id}`);
  };
  return (
    <Card
      title={problemName}
      style={{ width: "100%" }}
      hoverable
      onClick={onClick}
      headStyle={{ backgroundColor: getColor(solutionStatus) }}
    >
      <Row>
        <Latex>{"$$" + problem + "$$"}</Latex>
      </Row>
    </Card>
  );
};

export default ExerciseCard;
