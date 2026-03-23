package com.demo.product.infrastructure.persistence.adapter;

import com.demo.product.domain.model.PagedResult;
import com.demo.product.domain.model.Product;
import com.demo.product.domain.model.SortCriteria;
import com.demo.product.domain.port.ProductRepository;
import com.demo.product.infrastructure.persistence.SpringDataProductRepository;
import com.demo.product.infrastructure.persistence.entity.ProductEntity;
import com.demo.product.infrastructure.persistence.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Adaptador OFFSET con Spring Data JPA.
 *
 * Spring Data traduce Pageable a:
 *   SELECT * FROM products ORDER BY ? LIMIT ? OFFSET ?
 *   SELECT COUNT(*) FROM products   ← query extra para totalElements
 */
@Component
@RequiredArgsConstructor
public class JpaProductRepositoryAdapter implements ProductRepository {

    private final SpringDataProductRepository jpaRepository;
    private final ProductMapper mapper;

    @Override
    public PagedResult<Product> findAllPaged(int page, int size, SortCriteria sort, String category) {
        Sort jpaSort = buildJpaSort(sort);
        Pageable pageable = PageRequest.of(page, size, jpaSort);

        Page<ProductEntity> jpaPage = StringUtils.hasText(category)
                ? jpaRepository.findByCategory(category, pageable)
                : jpaRepository.findAll(pageable);

        List<Product> products = jpaPage.getContent()
                .stream()
                .map(mapper::toDomain)
                .toList();

        return PagedResult.ofOffset(products, jpaPage.getTotalElements(), page, size);
    }

    @Override
    public PagedResult<Product> findAllCursor(Long cursor, int size, SortCriteria sort) {
        throw new UnsupportedOperationException("Cursor pagination implementada en JdbcProductRepositoryAdapter");
    }

    /**
     * Anti-corruption layer: traduce SortCriteria del dominio → Sort de Spring Data.
     * Si mañana la entidad JPA renombra un campo, solo se toca este switch.
     */
    private Sort buildJpaSort(SortCriteria sort) {
        String entityField = mapDomainFieldToEntity(sort.field());
        Sort.Direction direction = sort.direction() == SortCriteria.SortDirection.ASC
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(direction, entityField);
    }

    private String mapDomainFieldToEntity(String domainField) {
        return switch (domainField) {
            case "createdAt" -> "createdAt";
            default -> domainField;
        };
    }
}
