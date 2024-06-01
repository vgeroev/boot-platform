import React from "react";
import { AuthContextProps, useAuth } from "react-oidc-context";
import Spinner from "../../component/spinner/Spinner";

interface Props { }

const WithAuthorization = <P,>(Component: React.FC<P>) => {
    return function With(props: P & Props) {
        const auth: AuthContextProps = useAuth();
        if (auth.isAuthenticated) {
            return <Component {...props} />;
        } else if (auth.isLoading) {
            return <Spinner />;
        } else {
            console.log(auth.error);
            auth.signinRedirect();
            return <></>;
        }
    };
};

export default WithAuthorization;
