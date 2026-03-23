package com.demo.product.domain.model;

import java.util.Set;

public record SortCriteria(String field, SortDirection direction) {

    public enum SortDirection { ASC, DESC }

    public static SortCriteria of(String field, String direction) {
        validateField(field);
        SortDirection dir = SortDirection.valueOf(direction.toUpperCase());
        return new SortCriteria(field, dir);
    }

    private static void validateField(String field) {
        Set<String> allowed = Set.of("id", "name", "price", "category", "createdAt");
        if (!allowed.contains(field)) {
            throw new IllegalArgumentException("Campo de ordenamiento no permitido: " + field);
        }
    }
}
