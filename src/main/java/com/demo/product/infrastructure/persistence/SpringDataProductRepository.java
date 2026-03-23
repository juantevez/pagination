package com.demo.product.infrastructure.persistence;

import com.demo.product.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataProductRepository extends JpaRepository<ProductEntity, Long> {
    Page<ProductEntity> findByCategory(String category, Pageable pageable);
}
