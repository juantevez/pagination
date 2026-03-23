package com.demo.product.application;

import com.demo.product.domain.model.PagedResult;
import com.demo.product.domain.model.Product;
import com.demo.product.domain.model.SortCriteria;
import com.demo.product.domain.port.ProductRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class GetProductsUseCase {

    private final ProductRepository offsetRepository;
    private final ProductRepository cursorRepository;

    public GetProductsUseCase(
            @Qualifier("jpaProductRepositoryAdapter")  ProductRepository offsetRepository,
            @Qualifier("jdbcProductRepositoryAdapter") ProductRepository cursorRepository
    ) {
        this.offsetRepository = offsetRepository;
        this.cursorRepository = cursorRepository;
    }

    public PagedResult<Product> getProductsPaged(
            int page, int size, String sortField, String sortDirection, String category
    ) {
        validatePageParams(page, size);
        SortCriteria sort = SortCriteria.of(sortField, sortDirection);
        return offsetRepository.findAllPaged(page, size, sort, category);
    }

    public PagedResult<Product> getProductsCursor(
            Long cursor, int size, String sortField, String sortDirection
    ) {
        validatePageSize(size);
        SortCriteria sort = SortCriteria.of(sortField, sortDirection);
        return cursorRepository.findAllCursor(cursor, size, sort);
    }

    private void validatePageParams(int page, int size) {
        if (page < 0) throw new IllegalArgumentException("El número de página no puede ser negativo");
        validatePageSize(size);
    }

    private void validatePageSize(int size) {
        if (size < 1 || size > 100)
            throw new IllegalArgumentException("El tamaño de página debe estar entre 1 y 100");
    }
}
