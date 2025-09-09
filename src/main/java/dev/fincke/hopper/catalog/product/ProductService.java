package dev.fincke.hopper.catalog.product;

import dev.fincke.hopper.catalog.product.dto.ProductCreateRequest;
import dev.fincke.hopper.catalog.product.dto.ProductResponse;
import dev.fincke.hopper.catalog.product.dto.ProductUpdateRequest;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for product business operations.
 * 
 * Handles all product-related business logic including inventory management,
 * validation, and CRUD operations. Separates business rules from data access.
 */
public interface ProductService 
{
    // * Core CRUD Operations
    
    // creates a new product with business validation
    ProductResponse createProduct(ProductCreateRequest request);
    
    // updates an existing product
    ProductResponse updateProduct(UUID id, ProductUpdateRequest request);
    
    // finds product by ID
    ProductResponse findById(UUID id);
    
    // retrieves all products
    List<ProductResponse> findAll();
    
    // deletes a product (if no dependencies exist)
    void deleteProduct(UUID id);
    
    // * Stock Management Operations
    
    // adjusts stock quantity (positive = increase, negative = decrease)
    ProductResponse adjustStock(UUID id, int quantityChange);
    
    // * Query Operations
    
    // finds products with stock below the specified threshold
    List<ProductResponse> findLowStockProducts(int threshold);
    
    // finds product by SKU
    ProductResponse findBySku(String sku);
    
    // finds products by name (case-insensitive partial match)
    List<ProductResponse> findByNameContaining(String name);
}