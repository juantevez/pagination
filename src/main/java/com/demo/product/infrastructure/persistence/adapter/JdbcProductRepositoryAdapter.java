package com.demo.product.infrastructure.persistence.adapter;

import com.demo.product.domain.model.PagedResult;
import com.demo.product.domain.model.Product;
import com.demo.product.domain.model.SortCriteria;
import com.demo.product.domain.port.ProductRepository;
import com.demo.product.infrastructure.persistence.entity.ProductEntity;
import com.demo.product.infrastructure.persistence.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adaptador CURSOR (Keyset Pagination) con JdbcClient.
 *
 * SQL generado:
 *   SELECT * FROM products
 *   WHERE id > :cursor           ← keyset condition (salta con índice)
 *   ORDER BY price ASC, id ASC   ← id siempre como tiebreaker
 *   LIMIT :size + 1              ← size+1 para detectar hasNext sin COUNT(*)
 */
@Component
@RequiredArgsConstructor
public class JdbcProductRepositoryAdapter implements ProductRepository {

    private final JdbcClient jdbcClient;
    private final ProductMapper mapper;

    @Override
    public PagedResult<Product> findAllPaged(int page, int size, SortCriteria sort, String category) {
        throw new UnsupportedOperationException("Offset pagination implementada en JpaProductRepositoryAdapter");
    }

    @Override
    public PagedResult<Product> findAllCursor(Long cursor, int size, SortCriteria sort) {
        int fetchSize = size + 1; // pedimos uno de más para detectar hasNext
        String sql = buildCursorQuery(sort, cursor);

        var querySpec = jdbcClient.sql(sql).param("size", fetchSize);
        if (cursor != null) {
            querySpec = querySpec.param("cursor", cursor);
        }

        List<ProductEntity> entities = querySpec
                .query(ProductEntity.class)
                .list();

        boolean hasNext = entities.size() > size;
        if (hasNext) {
            entities = entities.subList(0, size);
        }

        List<Product> products = entities.stream().map(mapper::toDomain).toList();

        String nextCursor = hasNext && !products.isEmpty()
                ? String.valueOf(products.get(products.size() - 1).id())
                : null;

        return PagedResult.ofCursor(products, size, nextCursor);
    }

    /**
     * Construye la query con keyset condition y sorting dinámico.
     * El ORDER BY siempre incluye id al final como tiebreaker para
     * garantizar un orden determinístico ante valores duplicados.
     */
    private String buildCursorQuery(SortCriteria sort, Long cursor) {
        String orderDirection = sort.direction().name();
        String sortField = toSnakeCase(sort.field());
        String whereClause = cursor != null ? "WHERE id > :cursor" : "";

        return """
                SELECT id, name, category, price, created_at
                FROM products
                %s
                ORDER BY %s %s, id ASC
                LIMIT :size
                """.formatted(whereClause, sortField, orderDirection);
    }

    private String toSnakeCase(String field) {
        return switch (field) {
            case "createdAt" -> "created_at";
            default -> field;
        };
    }
}
