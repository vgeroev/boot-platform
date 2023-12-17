import { Col, Divider, Input, Row, Table, Tag } from "antd";
import React from "react";
import { AuthState, useAuth } from "react-oidc-context";
import { useNavigate, useParams } from "react-router-dom";
import { PaginatedDto, Paging, Sorter } from "../../../utils/Paging";
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

type SolutionStatus = "UNSOLVED" | "SOLVED" | "UNSURE" | "PARTIALLY_SOLVED";
type SortField =
  | "CREATED_AT"
  | "UPDATED_AT"
  | "PROBLEM_NAME"
  | "SOLUTION_STATUS";

interface ExcerciseListElement {
  id: number;
  createdAt: string;
  updatedAt: string;
  problemName: string;
  solutionStatus: SolutionStatus;
}

interface ExercisePaginatedDto extends PaginatedDto<ExcerciseListElement> { }

interface PagingWithFilter extends Paging {
  problemNameFilter?: string;
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

    let params: any = {
      page: pagingWithFilter.page,
      pageSize: pagingWithFilter.pageSize,
    };
    if (pagingWithFilter.problemNameFilter) {
      params.problemNameFilter = pagingWithFilter.problemNameFilter.trim();
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
        let listElements: Array<ExcerciseListElement> = [];
        data.result.forEach((element: any) => {
          listElements.push({
            id: element.id as number,
            createdAt: dayjs(element.createdAt)
              .format("DD/MM/YYYY HH:mm")
              .toString(),
            updatedAt: element.updateAt
              ? dayjs(element.updateAt).format("DD/MM/YYYY HH:mm").toString()
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

function getSolutionTagColor(status: SolutionStatus): string {
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

function getColumns(): ColumnsType<ExcerciseListElement> {
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
      width: 100,
      dataIndex: "problemName",
      key: "problemName",
      fixed: "left",
      sorter: true,
    },
    {
      title: "Solution status",
      width: 25,
      dataIndex: "solutionStatus",
      key: "solutionStatus",
      fixed: "left",
      sorter: true,
      render: (value: SolutionStatus) => (
        <>
          <Tag color={getSolutionTagColor(value)} key={value}>
            {value}
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

  const [paging, setPaging] = React.useState<PagingWithFilter>({
    page: 0,
    pageSize: 24,
  });
  const [sorter, setSorter] = React.useState<Sorter<SortField> | undefined>(
    undefined,
  );

  const auth = useAuth();
  const [paginatedDto, setListElements] = React.useState<
    ExercisePaginatedDto | undefined
  >(undefined);

  React.useEffect(() => {
    const listSetter = () =>
      list(auth, exerciseSourceIdNumber, paging, sorter, (result) =>
        setListElements(result),
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
        {/* <Col> */}
        {/*   <Button type="primary" onClick={() => setModalOpen(true)}> */}
        {/*     Create exercise  */}
        {/*   </Button> */}
        {/**/}
        {/*   <Modal */}
        {/*     title="Create exercise source" */}
        {/*     open={modalOpen} */}
        {/*     onOk={() => { */}
        {/*       form.submit(); */}
        {/*       setModalOpen(false); */}
        {/*     }} */}
        {/*     onCancel={() => { */}
        {/*       setModalOpen(false); */}
        {/*       form.resetFields(); */}
        {/*     }} */}
        {/*   > */}
        {/*     {contextHolder} */}
        {/*     <Form */}
        {/*       form={form} */}
        {/*       onFinish={(values) => { */}
        {/*         createExSource( */}
        {/*           auth, */}
        {/*           values, */}
        {/*           (response) => */}
        {/*             list(auth, paging, (result) => setListElements(result)), */}
        {/*           (error) => { */}
        {/*             const errorCode = error.response?.data?.moduleError?.code; */}
        {/*             if (errorCode === "not_unique_domain_object") { */}
        {/*               messageApi.error({ content: "Not unique name" }); */}
        {/*             } else { */}
        {/*               messageApi.error({ content: "Unknown error" }); */}
        {/*             } */}
        {/*           }, */}
        {/*         ); */}
        {/*       }} */}
        {/*     > */}
        {/*       <Form.Item name="name" label="Name" rules={[{ required: true }]}> */}
        {/*         <Input defaultValue="" /> */}
        {/*       </Form.Item> */}
        {/*     </Form> */}
        {/*   </Modal> */}
        {/* </Col> */}

        <Col>
          <Input
            placeholder="Search"
            onChange={(e) => {
              setPaging({
                ...paging,
                page: 0,
                problemNameFilter: e.target.value,
                searching: true,
              });
            }}
          />
        </Col>
      </Row>

      <Divider orientation="center">Exercises</Divider>
      <Table
        bordered
        pagination={false}
        columns={getColumns()}
        onRow={(data, index) => ({
          onClick: () => navigate("/home"),
        })}
        onChange={(
          pagination: TablePaginationConfig,
          filters: Record<string, FilterValue | null>,
          sorter:
            | SorterResult<ExcerciseListElement>
            | SorterResult<ExcerciseListElement>[],
          extra: TableCurrentDataSource<ExcerciseListElement>,
        ) => {
          const castedSorted = sorter as SorterResult<ExcerciseListElement>;
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
        scroll={{ x: 1500, y: 300 }}
      />

      {/* <Row> */}
      {/*   <Pagination */}
      {/*     current={paging.page + 1} */}
      {/*     onChange={(pageNumber, pageSize) => */}
      {/*       setPaging({ page: pageNumber - 1, pageSize: pageSize }) */}
      {/*     } */}
      {/*     defaultCurrent={1} */}
      {/*     total={listElements.totalCount} */}
      {/*     pageSize={paging.pageSize} */}
      {/*     pageSizeOptions={[paging.pageSize]} */}
      {/*     hideOnSinglePage */}
      {/*   /> */}
      {/* </Row> */}
    </>
  );
};

export default ExerciseList;
