package dev.fincke.hopper.catalog.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

/**
 * Request DTO for updating an existing product.
 * 
 * All fields are optional - only provided fields will be updated.
 * Allows partial updates without requiring all product data.
 */
public record ProductUpdateRequest(
    // SKU (optional update, must be unique if provided)
    String sku,
    
    // name of product (optional update)
    String name,
    
    // description of product (optional update)
    String description,
    
    // price of product (optional update, must be positive if provided)
    @DecimalMin(value = "0.00", message = "Price must be positive")
    BigDecimal price,
    
    // stock quantity (optional update, must be non-negative if provided)
    @Min(value = 0, message = "Quantity cannot be negative")
    Integer quantity
) {
    // * Compact Constructor for Data Normalization
    
    public ProductUpdateRequest {
        // normalize string fields by trimming whitespace (only if not null)
        sku = sku != null ? sku.trim() : null;
        name = name != null ? name.trim() : null;
        description = description != null ? description.trim() : null;
    }
    
    // * Utility Methods
    
    // checks if any field has been provided for update
    public boolean hasUpdates() 
    {
        return sku != null || name != null || description != null || price != null || quantity != null;
    }
}