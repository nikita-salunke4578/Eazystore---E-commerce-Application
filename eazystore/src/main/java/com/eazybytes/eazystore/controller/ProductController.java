package com.eazybytes.eazystore.controller;

import com.eazybytes.eazystore.dto.ErrorResponseDto;
import com.eazybytes.eazystore.dto.ProductDto;
import com.eazybytes.eazystore.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService iProductService;

    @GetMapping
    public ResponseEntity<List<ProductDto>> getProducts() {
        List<ProductDto> productList = iProductService.getProducts();
        return ResponseEntity.ok().body(productList);
    }

    /**
     * Search products by keyword
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam String keyword) {
        List<ProductDto> results = iProductService.searchProductsByKeyword(keyword);
        return ResponseEntity.ok(results);
    }

    /**
     * Filter products by price range
     */
    @GetMapping("/filter/price")
    public ResponseEntity<List<ProductDto>> filterByPrice(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        List<ProductDto> results = iProductService.filterProductsByPrice(minPrice, maxPrice);
        return ResponseEntity.ok(results);
    }

    /**
     * Filter products by popularity
     */
    @GetMapping("/filter/popularity")
    public ResponseEntity<List<ProductDto>> filterByPopularity(
            @RequestParam Integer minPopularity,
            @RequestParam Integer maxPopularity) {
        List<ProductDto> results = iProductService.filterProductsByPopularity(minPopularity, maxPopularity);
        return ResponseEntity.ok(results);
    }

    /**
     * Get trending products
     */
    @GetMapping("/trending")
    public ResponseEntity<List<ProductDto>> getTrendingProducts(
            @RequestParam(defaultValue = "10") Integer limit) {
        List<ProductDto> results = iProductService.getTrendingProducts(limit);
        return ResponseEntity.ok(results);
    }

    /**
     * Get budget-friendly products (cheapest)
     */
    @GetMapping("/budget")
    public ResponseEntity<List<ProductDto>> getBudgetProducts(
            @RequestParam(defaultValue = "10") Integer limit) {
        List<ProductDto> results = iProductService.getBudgetProducts(limit);
        return ResponseEntity.ok(results);
    }

    /**
     * Get premium products (most expensive)
     */
    @GetMapping("/premium")
    public ResponseEntity<List<ProductDto>> getPremiumProducts(
            @RequestParam(defaultValue = "10") Integer limit) {
        List<ProductDto> results = iProductService.getPremiumProducts(limit);
        return ResponseEntity.ok(results);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception exception,
            WebRequest webRequest) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                webRequest.getDescription(false), HttpStatus.SERVICE_UNAVAILABLE,
                exception.getMessage(), Instant.now());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.SERVICE_UNAVAILABLE);
    }

}
