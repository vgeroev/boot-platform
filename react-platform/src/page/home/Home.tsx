import axios from "axios";
import React from "react";
import ApplicationList, {
  AppModule,
} from "../../component/application/ApplicationList";
import Spinner from "../../component/spinner/Spinner";
import { EXERCISE_SOURCE_LIST_PATH } from "../../module/exercises/route/ExercisesRouteGetter";
import { getMatrix } from "../../utils/GridUtils";
import { getModulesRequest, HttpRequest } from "../../utils/HttpUtils";

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
    path: EXERCISE_SOURCE_LIST_PATH,
  });

  return apps;
}

async function fetchApplications(
  onSuccess: (result: any) => void,
  onError: (e: Error) => void,
): Promise<void> {
  try {
    const httpRequest: HttpRequest = getModulesRequest();

    await axios
      .request<any>({
        url: httpRequest.url,
        method: httpRequest.method,
      })
      .then((response: any) => {
        onSuccess(response.data);
      })
      .catch(onError);
  } catch (e: any) {
    console.error(e);
  }
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
  const [appsMatrix, setAppsMatrix] = React.useState<AppModule[][] | undefined>(
    undefined,
  );

  const span: number = 4;

  React.useEffect(() => {
    fetchApplications(
      (data) => {
        const initializedApps: AppModule[] = getInitializedApps(
          data as string[],
        );
        const elementsPerRow: number = 24 / span;
        const cRow = initializedApps.length / elementsPerRow;
        setAppsMatrix(getMatrix(initializedApps, cRow, elementsPerRow));
      },
      (e) => console.log(e),
    );
  }, []);

  if (!appsMatrix) {
    return <Spinner />;
  }

  return (
    <>
      <div style={{ height: "100vh" }}>
        <ApplicationList span={span} applications={appsMatrix} />
      </div>
    </>
  );
};

export default Home;
