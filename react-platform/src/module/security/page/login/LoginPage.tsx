import { Button, Form, FormProps, Input, Modal, Spin, Typography } from "antd";
import React from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useHttpRequest } from "../../../../hook/useHttpRequestHook";
import { LoginForm, LoginRequest } from "../../service/request/LoginRequest";
import "./styles.css";

type FieldType = {
  username?: string;
  password?: string;
};

const LoginPage: React.FC<{}> = () => {
  const [loading, setLoading] = React.useState<boolean>(false);
  const loginRequest: LoginRequest = useHttpRequest(LoginRequest);
  const navigate = useNavigate();
  const location = useLocation();

  const submit: FormProps<FieldType>["onFinish"] = (values) => {
    const username: string | undefined = values.username?.trim();
    if (!username) {
      throw new Error("No username");
    }
    const password: string | undefined = values.password?.trim();
    if (!password) {
      throw new Error("No password");
    }

    const requestData: LoginForm = {
      username: username,
      password: password,
    };

    setLoading(true);
    loginRequest.exec({
      requestVariables: requestData,
      onCompletion: (response) => {
        const status = response.status;
        if (status === 200) {
          navigate("/", { replace: true });
        } else if (status === 401) {
          Modal.error({ content: "Invalid username or password" });
        } else {
          console.log(response);
          Modal.error({ content: "Unpredictable error" });
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
          className="loginForm"
          name="basic"
          onFinish={submit}
          autoComplete="off"
        >
          <Typography.Title>Login</Typography.Title>
          <Form.Item<FieldType>
            label="Username"
            name="username"
            rules={[{ required: true, message: "Please input your username!" }]}
          >
            <Input />
          </Form.Item>

          <Form.Item<FieldType>
            label="Password"
            name="password"
            rules={[{ required: true, message: "Please input your password!" }]}
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
export default LoginPage;
