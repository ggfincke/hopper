package dev.fincke.hopper.catalog.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

// DTO for partial product updates.
// Lets clients send only changed fields while validation still protects business rules.
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
    
    // checks if caller supplied at least one field to update
    public boolean hasUpdates() 
    {
        return sku != null || name != null || description != null || price != null || quantity != null;
    }
}
