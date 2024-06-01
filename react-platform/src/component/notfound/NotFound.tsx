import { Result } from "antd";

interface Props {
  message?: string;
}

const NotFound: React.FC<Props> = ({ message }: Props) => {
  return <Result status="404" title={message || "404"} />;
};

export default NotFound;
