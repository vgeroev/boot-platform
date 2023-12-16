import React from "react";

export interface ModuleRoute {
  path: string;
  element: React.ReactElement;
  withBaseLayout?: boolean;
}
