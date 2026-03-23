package com.demo.product.infrastructure.web;

import com.demo.product.application.GetProductsUseCase;
import com.demo.product.domain.model.PagedResult;
import com.demo.product.domain.model.Product;
import com.demo.product.infrastructure.web.dto.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final GetProductsUseCase getProductsUseCase;

    /**
     * OFFSET-BASED
     * GET /api/v1/products/paged?page=0&size=10&sortField=price&sortDirection=ASC&category=electronics
     */
    @GetMapping("/paged")
    public ResponseEntity<PagedResponse<Product>> getProductsPaged(
            @RequestParam(defaultValue = "0")   int page,
            @RequestParam(defaultValue = "10")  int size,
            @RequestParam(defaultValue = "id")  String sortField,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false)     String category
    ) {
        PagedResult<Product> result = getProductsUseCase.getProductsPaged(
                page, size, sortField, sortDirection, category
        );
        return ResponseEntity.ok(PagedResponse.fromOffset(result));
    }

    /**
     * CURSOR-BASED
     * GET /api/v1/products/cursor?cursor=42&size=10&sortField=price&sortDirection=ASC
     * Primera página: omitir el parámetro cursor
     */
    @GetMapping("/cursor")
    public ResponseEntity<PagedResponse<Product>> getProductsCursor(
            @RequestParam(required = false)     Long cursor,
            @RequestParam(defaultValue = "10")  int size,
            @RequestParam(defaultValue = "id")  String sortField,
            @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        PagedResult<Product> result = getProductsUseCase.getProductsCursor(
                cursor, size, sortField, sortDirection
        );
        return ResponseEntity.ok(PagedResponse.fromCursor(result));
    }
}
