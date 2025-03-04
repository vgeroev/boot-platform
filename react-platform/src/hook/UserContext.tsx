import React, { createContext } from "react";
import { ModelFactory } from "../model/BaseModel";
import { UserWithPrivilegesModel } from "../module/security/model/UserModelWithPrivileges";

export interface LoggedInUser {
  user?: UserWithPrivilegesModel;
  setUser: (u: UserWithPrivilegesModel | undefined) => void;
}

export const UserContext = createContext<LoggedInUser>({
  user: undefined,
  setUser: () => {},
});

export const UserProvider = ({ children }: any) => {
  const KEY = "loggedInUser";
  const [user, setUser] = React.useState(() => {
    const saved = localStorage.getItem(KEY);
    return saved
      ? new ModelFactory(UserWithPrivilegesModel).getModel(
          JSON.parse(saved).data,
        )
      : undefined;
  });

  React.useEffect(() => {
    if (user) {
      localStorage.setItem(KEY, JSON.stringify(user.getRawData()));
    } else {
      localStorage.removeItem(KEY);
    }
  }, [user]);

  return (
    <UserContext.Provider value={{ user, setUser }}>
      {children}
    </UserContext.Provider>
  );
};
