package com.eazybytes.eazystore.repository;

import com.eazybytes.eazystore.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * Search products by keyword in name or description
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY p.popularity DESC")
    List<Product> searchByKeyword(@Param("keyword") String keyword);
    
    /**
     * Filter products by price range
     */
    @Query("SELECT p FROM Product p WHERE p.price >= :minPrice AND p.price <= :maxPrice " +
           "ORDER BY p.price ASC")
    List<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                                    @Param("maxPrice") BigDecimal maxPrice);
    
    /**
     * Filter products by popularity range
     */
    @Query("SELECT p FROM Product p WHERE p.popularity >= :minPopularity AND p.popularity <= :maxPopularity " +
           "ORDER BY p.popularity DESC")
    List<Product> findByPopularityRange(@Param("minPopularity") Integer minPopularity,
                                        @Param("maxPopularity") Integer maxPopularity);
    
    /**
     * Advanced search with multiple filters
     */
    @Query("SELECT p FROM Product p WHERE " +
           "(:keywords IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keywords, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keywords, '%'))) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:minPopularity IS NULL OR p.popularity >= :minPopularity) " +
           "AND (:maxPopularity IS NULL OR p.popularity <= :maxPopularity) " +
           "ORDER BY " +
           "CASE WHEN :sortBy = 'price' THEN p.price ELSE NULL END, " +
           "CASE WHEN :sortBy = 'popularity' THEN p.popularity ELSE NULL END, " +
           "CASE WHEN :sortBy = 'name' THEN p.name ELSE NULL END, " +
           "CASE WHEN :sortBy = 'newest' THEN p.createdAt ELSE NULL END")
    List<Product> advancedSearch(
           @Param("keywords") String keywords,
           @Param("minPrice") BigDecimal minPrice,
           @Param("maxPrice") BigDecimal maxPrice,
           @Param("minPopularity") Integer minPopularity,
           @Param("maxPopularity") Integer maxPopularity,
           @Param("sortBy") String sortBy);
    
    /**
     * Find products sorted by popularity
     */
    @Query("SELECT p FROM Product p ORDER BY p.popularity DESC")
    List<Product> findByPopularityOrderByPopularityDesc();
    
    /**
     * Find products sorted by price (ascending)
     */
    @Query("SELECT p FROM Product p ORDER BY p.price ASC")
    List<Product> findByPriceOrderByPriceAsc();
    
    /**
     * Find products sorted by price (descending)
     */
    @Query("SELECT p FROM Product p ORDER BY p.price DESC")
    List<Product> findByPriceOrderByPriceDesc();
    
    /**
     * Find products sorted by creation date (newest first)
     */
    @Query("SELECT p FROM Product p ORDER BY p.createdAt DESC")
    List<Product> findNewestProducts();
}
