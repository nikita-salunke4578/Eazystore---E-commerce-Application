package com.eazybytes.eazystore.service;

import com.eazybytes.eazystore.dto.ProductDto;

import java.math.BigDecimal;
import java.util.List;

public interface IProductService {

    List<ProductDto> getProducts();

    /**
     * Advanced search with multiple optional filters.
     */
    List<ProductDto> advancedProductSearch(String keywords,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer minPopularity,
            Integer maxPopularity,
            String sortBy,
            String sortOrder,
            Integer limit);
    
    /**
     * Search products by keyword
     */
    List<ProductDto> searchProductsByKeyword(String keyword);
    
    /**
     * Filter products by price range
     */
    List<ProductDto> filterProductsByPrice(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Filter products by popularity
     */
    List<ProductDto> filterProductsByPopularity(Integer minPopularity, Integer maxPopularity);
    
    /**
     * Get trending products (highest popularity)
     */
    List<ProductDto> getTrendingProducts(Integer limit);
    
    /**
     * Get budget products sorted by price
     */
    List<ProductDto> getBudgetProducts(Integer limit);
    
    /**
     * Get premium products
     */
    List<ProductDto> getPremiumProducts(Integer limit);
}
