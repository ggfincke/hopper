package dev.fincke.hopper.catalog.product.dto;

import dev.fincke.hopper.catalog.product.Product;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Response DTO for product data.
 * 
 * Immutable representation of product data for API responses.
 * Prevents accidental entity modification and provides clean API contract.
 */
public record ProductResponse(
    // product ID
    UUID id,
    
    // SKU of product (may be null)
    String sku,
    
    // name of product
    String name,
    
    // description of product (may be null)
    String description,
    
    // price of product
    BigDecimal price,
    
    // current stock quantity
    int quantity
) {
    // * Static Factory Methods
    
    // creates ProductResponse from Product entity
    public static ProductResponse from(Product product) 
    {
        return new ProductResponse(
            product.getId(),
            product.getSku(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getQuantity()
        );
    }
    
}