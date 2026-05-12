package com.eazybytes.eazystore.service.impl;

import com.eazybytes.eazystore.dto.ProductDto;
import com.eazybytes.eazystore.entity.Product;
import com.eazybytes.eazystore.repository.ProductRepository;
import com.eazybytes.eazystore.service.IProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;

    @Cacheable("products")
    @Override
    public List<ProductDto> getProducts() {
        return productRepository.findAll()
                .stream().map(this::transformToDTO).collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> advancedProductSearch(String keywords, BigDecimal minPrice, BigDecimal maxPrice, Integer minPopularity, Integer maxPopularity, String sortBy, String sortOrder, Integer limit) {
        log.info("Executing advanced search with keywords: {}", keywords);

        List<Product> results;
        int resultLimit = limit != null ? limit : 10;

        // Execute search based on the parsed query
        results = productRepository.advancedSearch(
                keywords,
                minPrice,
                maxPrice,
                minPopularity,
                maxPopularity,
                sortBy != null ? sortBy : "popularity"
        );
        
        // Apply sorting order and limit
        if ("desc".equalsIgnoreCase(sortOrder)) {
            results = results.stream().limit(resultLimit).collect(Collectors.toList());
        } else {
            results = results.stream().limit(resultLimit).collect(Collectors.toList());
        }
        
        return results.stream()
                .map(this::transformToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> searchProductsByKeyword(String keyword) {
        log.info("Searching products by keyword: {}", keyword);
        return productRepository.searchByKeyword(keyword)
                .stream()
                .map(this::transformToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> filterProductsByPrice(BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("Filtering products by price range: {} - {}", minPrice, maxPrice);
        return productRepository.findByPriceRange(minPrice, maxPrice)
                .stream()
                .map(this::transformToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> filterProductsByPopularity(Integer minPopularity, Integer maxPopularity) {
        log.info("Filtering products by popularity: {} - {}", minPopularity, maxPopularity);
        return productRepository.findByPopularityRange(minPopularity, maxPopularity)
                .stream()
                .map(this::transformToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable("trending-products")
    public List<ProductDto> getTrendingProducts(Integer limit) {
        log.info("Fetching trending products");
        int resultLimit = limit != null ? limit : 10;
        return productRepository.findByPopularityOrderByPopularityDesc()
                .stream()
                .limit(resultLimit)
                .map(this::transformToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable("budget-products")
    public List<ProductDto> getBudgetProducts(Integer limit) {
        log.info("Fetching budget products");
        int resultLimit = limit != null ? limit : 10;
        return productRepository.findByPriceOrderByPriceAsc()
                .stream()
                .limit(resultLimit)
                .map(this::transformToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable("premium-products")
    public List<ProductDto> getPremiumProducts(Integer limit) {
        log.info("Fetching premium products");
        int resultLimit = limit != null ? limit : 10;
        return productRepository.findByPriceOrderByPriceDesc()
                .stream()
                .limit(resultLimit)
                .map(this::transformToDTO)
                .collect(Collectors.toList());
    }

    private ProductDto transformToDTO(Product product) {
        ProductDto productDto = new ProductDto();
        BeanUtils.copyProperties(product, productDto);
        productDto.setProductId(product.getId());
        return productDto;
    }
}
