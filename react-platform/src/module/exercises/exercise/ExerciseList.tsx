import { Button, Col, Divider, Input, Row, Select, Table, Tag } from "antd";
import React from "react";
import { AuthState, useAuth } from "react-oidc-context";
import { useNavigate, useParams } from "react-router-dom";
import { PaginatedDto, Sorter } from "../../../utils/Paging";
import Spinner from "../../../component/spinner/Spinner";
import { getExerciseListHttpRequest } from "../utils/ExercisesHttpUtils";
import { HttpRequest } from "../../../utils/HttpUtils";
import axios from "axios";
import { ColumnsType } from "antd/es/table";
import dayjs from "dayjs";
import {
  FilterValue,
  SorterResult,
  TableCurrentDataSource,
  TablePaginationConfig,
} from "antd/es/table/interface";
import { SolutionStatus } from "../utils/BasicTypes";
import {
  getCreateExercisePath,
  getExercisePath,
} from "../route/ExercisesRouteGetter";

type SortField =
  | "CREATED_AT"
  | "UPDATED_AT"
  | "PROBLEM_NAME"
  | "SOLUTION_STATUS";

interface ExerciseListElement {
  id: number;
  createdAt: string;
  updatedAt: string;
  problemName: string;
  solutionStatus: SolutionStatus;
}

interface ExercisePaginatedDto extends PaginatedDto<ExerciseListElement> { }

interface PagingWithFilter {
  problemNameFilter?: string;
  solutionStatusFilter?: SolutionStatus;
  searching?: boolean;
}

async function list(
  auth: AuthState,
  exerciseSourceId: number,
  pagingWithFilter: PagingWithFilter,
  sorter: Sorter<SortField> | undefined,
  listSetter: (result: ExercisePaginatedDto) => void,
): Promise<void> {
  try {
    const token = auth.user?.access_token;
    if (!token) {
      throw new Error("Failed to get token");
    }

    const httpRequest: HttpRequest =
      getExerciseListHttpRequest(exerciseSourceId);

    let params: any = {};
    if (pagingWithFilter.problemNameFilter) {
      params.problemNameFilter = pagingWithFilter.problemNameFilter.trim();
    }
    if (pagingWithFilter.solutionStatusFilter) {
      params.solutionStatusFilter = pagingWithFilter.solutionStatusFilter;
    }
    if (sorter) {
      params.sortField = sorter.field;
      params.sortDirection = sorter.direction;
    }

    await axios
      .request<any>({
        url: httpRequest.url,
        method: httpRequest.method,
        headers: {
          Authorization: `Bearer ${token}`,
        },
        params: params,
      })
      .then((response: any) => {
        const data = response.data;
        let listElements: Array<ExerciseListElement> = [];
        data.result.forEach((element: any) => {
          listElements.push({
            id: element.id as number,
            createdAt: dayjs(element.createdAt)
              .format("DD/MM/YYYY HH:mm")
              .toString(),
            updatedAt: element.updatedAt
              ? dayjs(element.updatedAt).format("DD/MM/YYYY HH:mm").toString()
              : "-",
            problemName: element.problemName as string,
            solutionStatus: element.solutionStatus as SolutionStatus,
          });
        });

        listSetter({
          page: data.page,
          pageSize: data.pageSize,
          totalCount: data.totalCount,
          result: listElements,
        });
      });
  } catch (e) {
    console.error(e);
  }
}

function getSolutionStatusTagColor(status: SolutionStatus): string {
  switch (status) {
    case "SOLVED":
      return "green";
    case "UNSOLVED":
      return "red";
    case "UNSURE":
      return "blue";
    case "PARTIALLY_SOLVED":
      return "yellow";
  }
}

function getSolutionStatusTagValue(status: SolutionStatus): string {
  switch (status) {
    case "SOLVED":
      return "SOLVED";
    case "UNSOLVED":
      return "UNSOLVED";
    case "UNSURE":
      return "UNSURE";
    case "PARTIALLY_SOLVED":
      return "PARTIALLY SOLVED";
  }
}

function getColumns(): ColumnsType<ExerciseListElement> {
  return [
    {
      title: "Created",
      width: 25,
      dataIndex: "createdAt",
      key: "createdAt",
      fixed: "left",
      sorter: true,
    },
    {
      title: "Updated",
      width: 25,
      dataIndex: "updatedAt",
      key: "updatedAt",
      fixed: "left",
      sorter: true,
    },
    {
      title: "Problem name",
      width: 750,
      dataIndex: "problemName",
      key: "problemName",
      fixed: "left",
      sorter: true,
    },
    {
      title: "Solution status",
      width: 10,
      dataIndex: "solutionStatus",
      key: "solutionStatus",
      fixed: "left",
      sorter: true,
      render: (value: SolutionStatus) => (
        <>
          <Tag color={getSolutionStatusTagColor(value)} key={value}>
            {getSolutionStatusTagValue(value)}
          </Tag>
        </>
      ),
    },
  ];
}

const ExerciseList: React.FC<{}> = () => {
  const navigate = useNavigate();
  const { exerciseSourceId } = useParams();
  const exerciseSourceIdNumber: number = Number(exerciseSourceId);

  const [paging, setPaging] = React.useState<PagingWithFilter>({});
  const [sorter, setSorter] = React.useState<Sorter<SortField> | undefined>(
    undefined,
  );

  const auth = useAuth();
  const [paginatedDto, setPaginatedDto] = React.useState<
    ExercisePaginatedDto | undefined
  >(undefined);

  React.useEffect(() => {
    const listSetter = () =>
      list(auth, exerciseSourceIdNumber, paging, sorter, (result) =>
        setPaginatedDto(result),
      );
    if (paging.searching) {
      const task = setTimeout(() => {
        listSetter();
        setPaging({ ...paging, searching: false });
      }, 1000);
      return () => clearTimeout(task);
    } else {
      listSetter();
    }
  }, [auth, exerciseSourceIdNumber, paging, sorter]);

  if (!paginatedDto) {
    return <Spinner />;
  }

  return (
    <>
      <Row gutter={16} style={{ paddingTop: "30px" }}>
        <Col>
          <Input
            placeholder="Search"
            onChange={(e) => {
              setPaging({
                ...paging,
                problemNameFilter: e.target.value,
                searching: true,
              });
            }}
          />
        </Col>

        <Col>
          <Select
            defaultValue={undefined}
            style={{ width: 120 }}
            onChange={(value) => {
              setPaging({
                ...paging,
                solutionStatusFilter: value === "ANY" ? undefined : value,
              });
            }}
            options={[
              {
                value: "ANY",
                label: "ANY",
              },
              {
                value: "UNSOLVED",
                label: "UNSOLVED",
              },
              {
                value: "SOLVED",
                label: "SOLVED",
              },
              {
                value: "UNSURE",
                label: "UNSURE",
              },
              {
                value: "PARTIALLY_SOLVED",
                label: "PARTIALLY SOLVED",
              },
            ]}
          />
        </Col>

        <Col>
          <Button
            type="primary"
            onClick={() =>
              navigate(getCreateExercisePath(exerciseSourceIdNumber))
            }
          >
            Create exercise
          </Button>
        </Col>
      </Row>

      <Divider orientation="center">
        Exercises, total count - {paginatedDto.totalCount}
      </Divider>
      <Table
        bordered
        pagination={false}
        columns={getColumns()}
        onRow={(data, index) => ({
          onClick: () => navigate(getExercisePath(data.id)),
        })}
        onChange={(
          pagination: TablePaginationConfig,
          filters: Record<string, FilterValue | null>,
          sorter:
            | SorterResult<ExerciseListElement>
            | SorterResult<ExerciseListElement>[],
          extra: TableCurrentDataSource<ExerciseListElement>,
        ) => {
          const castedSorted = sorter as SorterResult<ExerciseListElement>;
          const directionGetter = (d: string | null | undefined) =>
            d === "descend" ? "DESC" : "ASC";
          switch (castedSorted.field) {
            case "createdAt":
              return setSorter({
                field: "CREATED_AT",
                direction: directionGetter(castedSorted.order),
              });
            case "updatedAt":
              return setSorter({
                field: "UPDATED_AT",
                direction: directionGetter(castedSorted.order),
              });
            case "problemName":
              return setSorter({
                field: "PROBLEM_NAME",
                direction: directionGetter(castedSorted.order),
              });
            case "solutionStatus":
              return setSorter({
                field: "SOLUTION_STATUS",
                direction: directionGetter(castedSorted.order),
              });
          }
          setSorter(undefined);
        }}
        dataSource={paginatedDto.result}
      />
    </>
  );
};

export default ExerciseList;
