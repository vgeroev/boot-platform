import { Divider, Col, Row } from "antd";
import React from "react";
import { useNavigate } from "react-router-dom";
import Application from "./Application";

export interface AppModule {
  uuid: string;
  name: string;
  desc: string;
  path: string;
}

export interface Props {
  span: number;
  applications: AppModule[][];
}

const ApplicationList: React.FC<Props> = ({ span, applications }) => {
  const navigate = useNavigate();

  return (
    <>
      <Divider orientation="center">Applications</Divider>
      {applications.map((row, i) => {
        return (
          <Row key={i} gutter={16} style={{ padding: "8px 0" }}>
            {row.map((element) => {
              return (
                <Col
                  key={element.uuid}
                  className="gutter-row"
                  span={span}
                  style={{ padding: "0px 10px" }}
                >
                  <Application
                    key={element.uuid}
                    name={element.name}
                    desc={element.desc}
                    onClick={() => navigate(element.path)}
                  />
                </Col>
              );
            })}
          </Row>
        );
      })}
    </>
  );
};

export default ApplicationList;
