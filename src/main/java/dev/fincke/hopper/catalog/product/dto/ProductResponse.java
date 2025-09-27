package dev.fincke.hopper.catalog.product.dto;

import dev.fincke.hopper.catalog.product.Product;

import java.math.BigDecimal;
import java.util.UUID;

// Immutable product representation returned to API consumers.
// Prevents leaking JPA entities and keeps response payloads consistent.
public record ProductResponse(
    // product ID surfaced to clients
    UUID id,
    
    // SKU exposed for integrations (may be null)
    String sku,
    
    // human-readable product name
    String name,
    
    // optional product description
    String description,
    
    // sale price presented to clients
    BigDecimal price,
    
    // up-to-date inventory level shown to callers
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
