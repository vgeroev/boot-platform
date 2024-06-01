import { SettingOutlined } from "@ant-design/icons";
import { Dropdown, MenuProps, Space } from "antd";
import { useNavigate } from "react-router-dom";
import { getCreateArticleRoute } from "../module/mathsroadmap/route/MathsRoadMapRouteGetter";

const items: MenuProps["items"] = [
  {
    key: "maths-road-map",
    label: "Maths Road Map",
    children: [
      {
        key: "create-article",
        label: "Create article",
      },
    ],
  },
];

function createKey(key1: string, key2?: string): string {
  if (key2) {
    return `${key1}-${key2}`;
  }
  return key1;
}

const paths: Map<string, string> = new Map();
paths.set(
  createKey("maths-road-map", "create-article"),
  getCreateArticleRoute(),
);

const ModulesMenu: React.FC<{}> = () => {
  const navigate = useNavigate();
  return (
    <>
      <Dropdown
        menu={{
          items,
          onClick: (menu) => {
            const keyPath: string[] = menu.keyPath;
            const keyForPath: string = createKey(keyPath[1], keyPath[0]);
            const path: string | undefined = paths.get(keyForPath);
            if (!path) {
              throw new Error("Failed to resolve path");
            }
            navigate(path);
          },
        }}
      >
        <Space>
          <SettingOutlined style={{ fontSize: "20px" }} />
        </Space>
      </Dropdown>
    </>
  );
};

export default ModulesMenu;
