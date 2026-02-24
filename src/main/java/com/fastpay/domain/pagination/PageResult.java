package com.fastpay.domain.pagination;

import java.util.List;

public record PageResult<T>(
        List<T> data,
        int currentPage,
        int totalPages,
        long totalElements
) {
}