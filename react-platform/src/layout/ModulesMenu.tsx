import { SettingOutlined } from "@ant-design/icons";
import { Dropdown, MenuProps, Space } from "antd";
import { useContext } from "react";
import { useNavigate } from "react-router-dom";
import { UserContext } from "../hook/UserContext";
import { getTagsPageRequest } from "../module/core/route/CoreRouteGetter";
import { getCreateArticleRoute } from "../module/maths-road-map/route/MathsRoadMapRouteGetter";
import { AccessOp } from "../module/security/model/AccessOp";
import { CORE_TAG_MANAGING_KEY } from "../utils/PrivilegeUtils";

function createKey(key1: string, key2?: string): string {
  if (key2) {
    return `${key1}-${key2}`;
  }
  return key1;
}

const ModulesMenu: React.FC<{}> = () => {
  const navigate = useNavigate();
  const { user } = useContext(UserContext);

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
  const paths: Map<string, string> = new Map();
  paths.set(
    createKey("maths-road-map", "create-article"),
    getCreateArticleRoute(),
  );
  if (user) {
    const privileges: Map<string, AccessOp[]> = user?.privileges;
    if (hasTagsPagePermission(privileges)) {
      items.push({
        key: "core-tags",
        label: "Tags",
      });
      paths.set(createKey("core-tags"), getTagsPageRequest());
    }
  }

  return (
    <>
      <Dropdown
        menu={{
          items,
          onClick: (menu) => {
            const keyPath: string[] = menu.keyPath;
            let keyForPath: string;
            if (keyPath.length === 1) {
              keyForPath = createKey(keyPath[0]);
            } else if (keyPath.length === 2) {
              keyForPath = createKey(keyPath[1], keyPath[0]);
            } else {
              throw new Error("Invalid key path " + keyPath);
            }
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

function hasTagsPagePermission(privileges: Map<string, AccessOp[]>): boolean {
  const tagsPrivileges: AccessOp[] | undefined = privileges.get(
    CORE_TAG_MANAGING_KEY,
  );
  if (!tagsPrivileges) {
    return false;
  }
  return (
    tagsPrivileges.includes(AccessOp.WRITE) &&
    tagsPrivileges.includes(AccessOp.DELETE)
  );
}

export default ModulesMenu;
