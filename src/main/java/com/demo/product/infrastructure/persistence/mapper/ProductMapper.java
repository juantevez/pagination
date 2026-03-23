package com.demo.product.infrastructure.persistence.mapper;

import com.demo.product.domain.model.Product;
import com.demo.product.infrastructure.persistence.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toDomain(ProductEntity entity) {
        return new Product(
                entity.getId(),
                entity.getName(),
                entity.getCategory(),
                entity.getPrice(),
                entity.getCreatedAt()
        );
    }

    public ProductEntity toEntity(Product domain) {
        return new ProductEntity(
                domain.id(),
                domain.name(),
                domain.category(),
                domain.price(),
                domain.createdAt()
        );
    }
}
