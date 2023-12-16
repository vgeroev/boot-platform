import { Card } from "antd";

interface Props {
  name: string;
  desc: string;
  onClick: () => void;
}

const Application: React.FC<Props> = ({ name, desc, onClick }: Props) => {
  return (
    <>
      <Card
        title={name}
        bordered={true}
        hoverable
        onClick={onClick}
        style={{ width: 300, textAlign: "center", fontSize: 15 }}
      >
        <p>{desc}</p>
      </Card>
    </>
  );
};

export default Application;
