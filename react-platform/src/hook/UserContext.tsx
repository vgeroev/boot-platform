import React, { createContext } from "react";
import { UserModel } from "../module/security/model/UserModel";

export interface LoggedInUser {
  user?: UserModel;
  setUser: (u: UserModel | undefined) => void;
}

export const UserContext = createContext<LoggedInUser>({
  user: undefined,
  setUser: () => {},
});

export const UserProvider = ({ children }: any) => {
  const KEY = "loggedInUser";
  const [user, setUser] = React.useState(() => {
    const saved = localStorage.getItem(KEY);
    return saved ? JSON.parse(saved) : null;
  });

  React.useEffect(() => {
    if (user) {
      localStorage.setItem(KEY, JSON.stringify(user));
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
