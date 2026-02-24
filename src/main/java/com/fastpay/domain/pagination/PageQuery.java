package com.fastpay.domain.pagination;

public record PageQuery(
        int pageNumber,
        int pageSize,
        String sortField,
        String sortDirection
) {
}