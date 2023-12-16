import {
  Button,
  Col,
  Divider,
  Empty,
  Form,
  Input,
  message,
  Modal,
  Pagination,
  Row,
} from "antd";
import axios from "axios";
import React from "react";
import { AuthState, useAuth } from "react-oidc-context";
import Spinner from "../../../component/spinner/Spinner";
import { getMatrix } from "../../../utils/GridUtils";
import { HttpRequest } from "../../../utils/HttpUtils";
import { PaginatedDto, Paging } from "../../../utils/Paging";
import {
  EXCERCISE_SOURCE_CREATE_HTTP_REQUEST,
  EXCERISE_SOURCE_LIST_HTTP_REQUEST,
} from "../utils/ExercisesHttpUtils";
import ExerciseSourceListElement from "./ExerciseSourceListElement";

interface PagingWithFilter extends Paging {
  nameFilter?: string;
  searching?: boolean;
}

interface ExerciseSourceListElementDto {
  id: number;
  name: string;
  createdAt: string;
  updatedAt: string;
}

interface ExerciseSourcePaginatedDto
  extends PaginatedDto<ExerciseSourceListElementDto> {}

async function createExSource(
  auth: AuthState,
  values: any,
  onSuccess: (response: any) => void,
  onError: (error: any) => void,
): Promise<void> {
  try {
    const token = auth.user?.access_token;
    if (!token) {
      throw new Error("Failed to get token");
    }

    const httpRequest: HttpRequest = EXCERCISE_SOURCE_CREATE_HTTP_REQUEST;

    await axios
      .request<any>({
        url: httpRequest.url,
        method: httpRequest.method,
        headers: {
          Authorization: `Bearer ${token}`,
        },
        data: {
          name: values.name,
        },
      })
      .then(onSuccess)
      .catch(onError);
  } catch (e) {
    console.error(e);
  }
}

async function list(
  auth: AuthState,
  pagingWithFilter: PagingWithFilter,
  listSetter: (result: ExerciseSourcePaginatedDto) => void,
): Promise<void> {
  try {
    const token = auth.user?.access_token;
    if (!token) {
      throw new Error("Failed to get token");
    }

    const httpRequest: HttpRequest = EXCERISE_SOURCE_LIST_HTTP_REQUEST;

    let params: any = {
      page: pagingWithFilter.page,
      pageSize: pagingWithFilter.pageSize,
    };
    if (pagingWithFilter.nameFilter) {
      params.nameFilter = pagingWithFilter.nameFilter.trim();
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
        listSetter(response.data as ExerciseSourcePaginatedDto);
      });
  } catch (e) {
    console.error(e);
  }
}

const ExerciseSourceList: React.FC<{}> = () => {
  const [paging, setPaging] = React.useState<PagingWithFilter>({
    page: 0,
    pageSize: 24,
  });
  const [modalOpen, setModalOpen] = React.useState(false);
  const [messageApi, contextHolder] = message.useMessage();
  const auth = useAuth();
  const [listElements, setListElements] = React.useState<
    ExerciseSourcePaginatedDto | undefined
  >(undefined);
  const [form] = Form.useForm();

  React.useEffect(() => {
    const listSetter = () =>
      list(auth, paging, (result) => setListElements(result));
    if (paging.searching) {
      const task = setTimeout(() => {
        listSetter();
        setPaging({ ...paging, searching: false });
      }, 1000);
      return () => clearTimeout(task);
    } else {
      listSetter();
    }
  }, [auth, paging]);

  if (!listElements) {
    return <Spinner />;
  }

  const elements: Array<ExerciseSourceListElementDto> = listElements?.result;

  const span: number = 4;
  const cCol: number = 24 / span;
  const cRow = elements.length / cCol;
  const matrix: Array<Array<ExerciseSourceListElementDto>> = getMatrix(
    elements,
    cRow,
    cCol,
  );

  return (
    <>
      <Row gutter={16} style={{ paddingTop: "30px" }}>
        <Col>
          <Button type="primary" onClick={() => setModalOpen(true)}>
            Create exercise source
          </Button>

          <Modal
            title="Create exercise source"
            open={modalOpen}
            onOk={() => {
              form.submit();
              setModalOpen(false);
            }}
            onCancel={() => {
              setModalOpen(false);
              form.resetFields();
            }}
          >
            {contextHolder}
            <Form
              form={form}
              onFinish={(values) => {
                createExSource(
                  auth,
                  values,
                  (response) =>
                    list(auth, paging, (result) => setListElements(result)),
                  (error) => {
                    const errorCode = error.response?.data?.moduleError?.code;
                    if (errorCode === "not_unique_domain_object") {
                      messageApi.error({ content: "Not unique name" });
                    } else {
                      messageApi.error({ content: "Unknown error" });
                    }
                  },
                );
              }}
            >
              <Form.Item name="name" label="Name" rules={[{ required: true }]}>
                <Input defaultValue="" />
              </Form.Item>
            </Form>
          </Modal>
        </Col>

        <Col>
          <Input
            placeholder="Search"
            onChange={(e) => {
              setPaging({
                ...paging,
                page: 0,
                nameFilter: e.target.value,
                searching: true,
              });
            }}
          />
        </Col>
      </Row>

      <Divider orientation="center">Exercise sources</Divider>

      {matrix.length === 0 && <Empty />}
      {matrix.map((row, i) => {
        return (
          <Row key={i} gutter={16} style={{ padding: "8px 0" }}>
            {row.map((element) => {
              return (
                <Col
                  key={element.id}
                  className="gutter-row"
                  span={span}
                  style={{ padding: "0px 10px" }}
                >
                  <ExerciseSourceListElement
                    key={element.id}
                    id={element.id}
                    name={element.name}
                    createdAt={element.createdAt}
                    updatedAt={element.updatedAt}
                  />
                </Col>
              );
            })}
          </Row>
        );
      })}
      <Row>
        <Pagination
          current={paging.page + 1}
          onChange={(pageNumber, pageSize) =>
            setPaging({ page: pageNumber - 1, pageSize: pageSize })
          }
          defaultCurrent={1}
          total={listElements.totalCount}
          pageSize={paging.pageSize}
          pageSizeOptions={[paging.pageSize]}
          hideOnSinglePage
        />
      </Row>
    </>
  );
};

export default ExerciseSourceList;
