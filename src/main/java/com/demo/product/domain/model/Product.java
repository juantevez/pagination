package com.demo.product.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record Product(
        Long id,
        String name,
        String category,
        BigDecimal price,
        Instant createdAt
) {}
