package com.demo.product.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_products_price_id",    columnList = "price ASC, id ASC"),
        @Index(name = "idx_products_name_id",     columnList = "name ASC, id ASC"),
        @Index(name = "idx_products_category_id", columnList = "category ASC, id ASC"),
        @Index(name = "idx_products_created_id",  columnList = "created_at ASC, id ASC")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal price;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
