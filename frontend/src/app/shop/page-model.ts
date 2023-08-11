export interface Page<T> {
    content: T[];
    pageable: {
        sort: {
        sorted: boolean;
        unsorted: boolean;
        empty: boolean;
        },
        offset: number;
        pageSize: number;
        pageNumber: number;
        unpaged: boolean;
        paged: boolean;
    };
    totalPages: number;
    totalElements: number;
    last: boolean;
    first: boolean;
    sort: {
        sorted: boolean;
        unsorted: boolean;
        empty: boolean;
    };
    numberOfElements: number;
    size: number;
    number: number;
    empty: boolean;
}