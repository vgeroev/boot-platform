import React from "react";

interface Props { }

const WithAuthorization = <P,>(Component: React.FC<P>) => {
    return function With(props: P & Props) {
        return <Component {...props} />;
        // const auth: AuthContextProps = useAuth();
        // if (auth.isAuthenticated) {
        //     return <Component {...props} />;
        // } else if (auth.isLoading) {
        //     return <Spinner />;
        // } else {
        //     console.log(auth.error);
        //     auth.signinRedirect();
        //     return <></>;
        // }
    };
};

export default WithAuthorization;
