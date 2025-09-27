package dev.fincke.hopper.catalog.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

// DTO for product creation requests.
// Applies validation rules so invalid inventory never reaches the domain layer.
public record ProductCreateRequest(
    // SKU (optional, must be unique if provided)
    String sku,
    
    // name of product (required)
    @NotBlank(message = "Product name is required")
    String name,
    
    // description of product (optional)
    String description,
    
    // price of product (required, must be positive)
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", message = "Price must be positive")
    BigDecimal price,
    
    // initial stock quantity (must be non-negative)
    @Min(value = 0, message = "Quantity cannot be negative")
    int quantity
) {
    // * Compact Constructor for Data Normalization
    
    public ProductCreateRequest {
        // normalize string fields by trimming whitespace
        sku = sku != null ? sku.trim() : null;
        name = name != null ? name.trim() : name;
        description = description != null ? description.trim() : description;
        
        // ensure quantity defaults to 0 if caller omits or passes negative values
        quantity = Math.max(0, quantity);
    }
}
