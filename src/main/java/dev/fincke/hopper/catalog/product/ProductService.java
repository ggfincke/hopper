package dev.fincke.hopper.catalog.product;

import dev.fincke.hopper.catalog.product.dto.ProductCreateRequest;
import dev.fincke.hopper.catalog.product.dto.ProductResponse;
import dev.fincke.hopper.catalog.product.dto.ProductUpdateRequest;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// Service interface for product business operations (separates business rules from data access)
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

    // retrieves products with pagination support
    Page<ProductResponse> findAll(Pageable pageable);
    
    // deletes a product (if no dependencies exist)
    void deleteProduct(UUID id);
    
    // * Stock Management Operations
    
    // adjusts stock quantity (positive = increase, negative = decrease)
    ProductResponse adjustStock(UUID id, int quantityChange);
    
    // * Query Operations
    
    // finds products with stock below the specified threshold
    List<ProductResponse> findLowStockProducts(int threshold);

    // finds products with stock below the specified threshold, paginated
    Page<ProductResponse> findLowStockProducts(int threshold, Pageable pageable);
    
    // finds product by SKU
    ProductResponse findBySku(String sku);
    
    // finds products by name (case-insensitive partial match)
    List<ProductResponse> findByNameContaining(String name);

    // finds products by name (case-insensitive partial match) with pagination
    Page<ProductResponse> findByNameContaining(String name, Pageable pageable);
}
