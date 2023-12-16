export interface HttpRequest {
  method: "get" | "post" | "delete" | "patch" | "put";
  url: string;
}
