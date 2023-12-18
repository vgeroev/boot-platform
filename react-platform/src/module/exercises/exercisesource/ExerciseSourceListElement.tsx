import { Card } from "antd";
import dayjs from "dayjs";
import React from "react";
import { useNavigate } from "react-router-dom";
import { getExerciseListPath } from "../route/ExercisesRouteGetter";

interface Props {
  id: number;
  name: string;
  createdAt: string;
  updatedAt: string;
}

const ExerciseSourceListElement: React.FC<Props> = ({
  id,
  name,
  createdAt,
  updatedAt,
}: Props) => {
  const navigate = useNavigate();
  return (
    <Card
      title={name}
      bordered={true}
      hoverable
      onClick={() => navigate(getExerciseListPath(id))}
      style={{ width: 300, textAlign: "center", fontSize: 15 }}
    >
      <p>Created: {dayjs(createdAt).format("DD/MM/YYYY HH:mm")}</p>
    </Card>
  );
};

export default ExerciseSourceListElement;
