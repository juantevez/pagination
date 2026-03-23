package com.demo.product.domain.port;

import com.demo.product.domain.model.PagedResult;
import com.demo.product.domain.model.Product;
import com.demo.product.domain.model.SortCriteria;

public interface ProductRepository {

    /**
     * Paginación OFFSET-BASED.
     * Usa LIMIT + OFFSET en SQL. Simple pero se degrada con OFFSET alto.
     */
    PagedResult<Product> findAllPaged(int page, int size, SortCriteria sort, String category);

    /**
     * Paginación CURSOR-BASED (Keyset).
     * Usa WHERE id > :cursor. Performance constante en tablas grandes.
     */
    PagedResult<Product> findAllCursor(Long cursor, int size, SortCriteria sort);
}
