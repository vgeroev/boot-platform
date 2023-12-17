export interface PaginatedDto<T> {
    result: Array<T>;
    totalCount: number;
    page: number;
    pageSize: number;
}

export interface Paging {
    page: number;
    pageSize: number;
}

export type SortDirection = "ASC" | "DESC";

export interface Sorter<T> {
    field: T;
    direction: SortDirection;
}
