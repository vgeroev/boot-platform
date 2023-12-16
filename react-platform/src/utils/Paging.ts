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
