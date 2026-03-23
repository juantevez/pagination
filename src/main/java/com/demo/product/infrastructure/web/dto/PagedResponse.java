package com.demo.product.infrastructure.web.dto;

import com.demo.product.domain.model.PagedResult;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PagedResponse<T>(
        List<T> content,
        PageMetadata page,
        NavigationMetadata navigation
) {

    public static <T> PagedResponse<T> fromOffset(PagedResult<T> result) {
        return new PagedResponse<>(
                result.content(),
                PageMetadata.forOffset(result.currentPage(), result.pageSize(),
                        result.totalElements(), result.totalPages()),
                NavigationMetadata.forOffset(result.hasNext(), result.hasPrevious())
        );
    }

    public static <T> PagedResponse<T> fromCursor(PagedResult<T> result) {
        return new PagedResponse<>(
                result.content(),
                PageMetadata.forCursor(result.pageSize()),
                NavigationMetadata.forCursor(result.hasNext(), result.nextCursor())
        );
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record PageMetadata(
            Integer current,
            int size,
            Long totalElements,
            Integer totalPages
    ) {
        static PageMetadata forOffset(int current, int size, long total, int totalPages) {
            return new PageMetadata(current, size, total, totalPages);
        }
        static PageMetadata forCursor(int size) {
            return new PageMetadata(null, size, null, null);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record NavigationMetadata(
            boolean hasNext,
            Boolean hasPrevious,
            String nextCursor
    ) {
        static NavigationMetadata forOffset(boolean hasNext, boolean hasPrevious) {
            return new NavigationMetadata(hasNext, hasPrevious, null);
        }
        static NavigationMetadata forCursor(boolean hasNext, String nextCursor) {
            return new NavigationMetadata(hasNext, null, nextCursor);
        }
    }
}
