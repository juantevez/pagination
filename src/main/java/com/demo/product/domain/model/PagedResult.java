package com.demo.product.domain.model;

import java.util.List;

public record PagedResult<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int currentPage,
        int pageSize,
        boolean hasNext,
        boolean hasPrevious,
        String nextCursor
) {

    public static <T> PagedResult<T> ofOffset(
            List<T> content,
            long totalElements,
            int currentPage,
            int pageSize
    ) {
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        boolean hasNext = currentPage < totalPages - 1;
        boolean hasPrevious = currentPage > 0;
        return new PagedResult<>(content, totalElements, totalPages, currentPage, pageSize, hasNext, hasPrevious, null);
    }

    public static <T> PagedResult<T> ofCursor(
            List<T> content,
            int pageSize,
            String nextCursor
    ) {
        boolean hasNext = nextCursor != null;
        return new PagedResult<>(content, -1, -1, -1, pageSize, hasNext, false, nextCursor);
    }
}
