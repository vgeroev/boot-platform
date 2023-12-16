import { Card } from "antd";
import React from "react";
import { useNavigate } from "react-router-dom";

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
      onClick={() => navigate("/home")}
      style={{ width: 300, textAlign: "center", fontSize: 15 }}
    >
      <p>{createdAt}</p>
    </Card>
  );
};

export default ExerciseSourceListElement;
