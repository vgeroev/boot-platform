import { AuthContextProps, useAuth } from "react-oidc-context";

// export const usePlatformAuth: PlatformAuthProps = () => {
//   const auth: AuthContextProps = useAuth();
//
//   if (auth.isLoading) {
//     return <Spinner />;
//   } else if (auth.error) {
//     console.error("Auth error", auth.error);
//     throw new Error("Auth error: " + auth.error) ;
//   }
//
//   let authStatusBar: React.ReactElement;
//   if (auth.isAuthenticated) {
//     const username: string = auth.user?.profile.preferred_username || "";
//     const onLogout = () => {
//       auth.removeUser();
//       auth.signoutRedirect();
//     };
//   }
// };
//
// export interface PlatformAuthProps {
//     authenticated: boolean;
//     username: string;
// }
