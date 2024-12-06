import { Button, Form, FormProps, Input, Modal, Spin, Typography } from "antd";
import React from "react";
import { useNavigate } from "react-router-dom";
import { useHttpRequest } from "../../../../hook/useHttpRequestHook";
import { getLoginRoute } from "../../route/SecurityRoutes";
import {
  RegisterForm,
  RegisterRequest,
} from "../../service/request/RegisterRequest";
import "./styles.css";

type FieldType = {
  username?: string;
  password?: string;
};

const RegisterPage: React.FC<{}> = () => {
  const [loading, setLoading] = React.useState<boolean>(false);
  const registerRequest: RegisterRequest = useHttpRequest(RegisterRequest);
  const navigate = useNavigate();

  const submit: FormProps<FieldType>["onFinish"] = (values) => {
    const username: string | undefined = values.username?.trim();
    if (!username) {
      throw new Error("No username");
    }
    const password: string | undefined = values.password?.trim();
    if (!password) {
      throw new Error("No password");
    }

    const requestData: RegisterForm = {
      username: username,
      password: password,
    };

    setLoading(true);
    registerRequest.exec({
      data: requestData,
      onSuccess: () => {
        navigate(getLoginRoute());
      },
      handleModuleError: (httpResponse) => {
        switch (httpResponse.data.code) {
          case "not_unique_domain_object":
            Modal.error({
              title: "User with such username already exists",
            });
            return true;
          default:
            return false;
        }
      },
      onFinally: () => {
        setLoading(false);
      },
    });
  };

  return (
    <Spin delay={100} spinning={loading}>
      <div className="bg">
        <Form
          className="registerForm"
          name="basic"
          onFinish={submit}
          autoComplete="off"
        >
          <Typography.Title>Registration</Typography.Title>
          <Form.Item<FieldType>
            label="Username"
            name="username"
            rules={[
              { required: true, message: "Please input your username!" },
              { min: 5, message: "Username should contain at least 5 symbols" },
            ]}
          >
            <Input />
          </Form.Item>

          <Form.Item<FieldType>
            label="Password"
            name="password"
            rules={[
              { required: true, message: "Please input your password!" },
              { min: 8, message: "Password should contain at least 8 symbols" },
            ]}
          >
            <Input.Password />
          </Form.Item>

          <Form.Item
            name="confirm"
            label="Confirm Password"
            dependencies={["password"]}
            hasFeedback
            rules={[
              {
                required: true,
                message: "Please confirm your password!",
              },
              ({ getFieldValue }) => ({
                validator(rule, value) {
                  if (!value || getFieldValue("password") === value) {
                    return Promise.resolve();
                  }

                  return Promise.reject("The two passwords do not match!");
                },
              }),
            ]}
          >
            <Input.Password />
          </Form.Item>
          <Form.Item label={null}>
            <Button type="primary" htmlType="submit">
              Submit
            </Button>
          </Form.Item>
        </Form>
      </div>
    </Spin>
  );
};

export default RegisterPage;
