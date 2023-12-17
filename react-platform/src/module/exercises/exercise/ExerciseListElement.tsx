import { Card } from "antd";
import dayjs from "dayjs";
import React from "react";
import { useNavigate } from "react-router-dom";

interface ExcerciseListElementProps {
  id: number;
  createdAt: string;
  updatedAt: string;
  problemName: string;
  solutionStatus: "UNSOLVED" | "SOLVED" | "UNSURE";
}
const ExerciseListElement: React.FC<ExcerciseListElementProps> = ({
  id,
  createdAt,
  updatedAt,
  problemName,
  solutionStatus,
}: ExcerciseListElementProps) => {
  const navigate = useNavigate();
  return (
    <Card
      title={problemName}
      bordered={true}
      hoverable
      onClick={() => navigate("/home")}
      style={{ width: 300, textAlign: "center", fontSize: 15 }}
    >
      <p>Created: {dayjs(createdAt).format("DD/MM/YYYY HH:mm")}</p>
      <p>Updated: {dayjs(updatedAt).format("DD/MM/YYYY HH:mm")}</p>
      <p>Solution status: {solutionStatus}</p>
    </Card>
  );
};

export default ExerciseListElement;
