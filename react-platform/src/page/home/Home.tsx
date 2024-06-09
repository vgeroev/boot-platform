import { Spin } from "antd";
import React from "react";
import ApplicationList, {
  AppModule,
} from "../../component/application/ApplicationList";
import { useHttpRequest } from "../../hook/useHttpRequestHook";
import { EXERCISE_SOURCE_LIST_PATH } from "../../module/exercises/route/ExercisesRouteGetter";
import { getArticleListRoute } from "../../module/mathsroadmap/route/MathsRoadMapRouteGetter";
import { InitializedModulesRequest } from "../../service/request/InitializedModulesRequest";
import { getMatrix } from "../../utils/GridUtils";

const applications: Map<string, AppModule> = initApplicationMap();

function initApplicationMap(): Map<string, AppModule> {
  const apps: Map<string, AppModule> = new Map();

  apps.set("org.vmalibu.module.exercises", {
    uuid: "org.vmalibu.module.exercises",
    name: "Exercises",
    desc: "Your exercise solvings",
    path: EXERCISE_SOURCE_LIST_PATH,
  });

  apps.set("org.vmalibu.module.mathsroadmap", {
    uuid: "org.vmalibu.module.mathsroadmap",
    name: "Maths Road Map",
    desc: "Articles for study math",
    path: getArticleListRoute(),
  });

  return apps;
}

function getInitializedApps(uuids: string[]): Array<AppModule> {
  const apps: Array<AppModule> = [];
  uuids.forEach((uuid) => {
    if (applications.has(uuid)) {
      apps.push(applications.get(uuid)!);
    }
  });

  return apps;
}

const Home: React.FC<{}> = () => {
  const [loading, setLoading] = React.useState<boolean>(false);
  const [appsMatrix, setAppsMatrix] = React.useState<AppModule[][] | undefined>(
    undefined,
  );
  const initializedModulesRequest: InitializedModulesRequest = useHttpRequest(
    InitializedModulesRequest,
  );

  const span: number = 4;
  React.useEffect(() => {
    setLoading(true);
    initializedModulesRequest.exec({
      onSuccess: (response) => {
        const moduleUUIDs: string[] = response.data.moduleUUIDs || [];
        const initializedApps: AppModule[] = getInitializedApps(moduleUUIDs);
        const elementsPerRow: number = 24 / span;
        const cRow = initializedApps.length / elementsPerRow;
        setAppsMatrix(getMatrix(initializedApps, cRow, elementsPerRow));
      },
      onFinally: () => setLoading(false),
    });
  }, []);

  return (
    <Spin spinning={loading}>
      <div style={{ height: "100vh" }}>
        <ApplicationList span={span} applications={appsMatrix || [[]]} />
      </div>
    </Spin>
  );
};

export default Home;
