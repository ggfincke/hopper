package dev.fincke.hopper.order.item.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

// Request DTO for creating new order items (immutable, validates business rules)
public record OrderItemCreateRequest(
    
    // Order ID where this item belongs (required)
    @NotNull(message = "Order ID is required")
    UUID orderId,
    
    // Listing ID that this item references (required)
    @NotNull(message = "Listing ID is required")
    UUID listingId,
    
    // Quantity to order (required, minimum 1)
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity,
    
    // Unit price for this item (required, minimum 0.00)
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", message = "Price must be non-negative")
    BigDecimal price
    
)
{
    
    // Custom validation for business rules
    public OrderItemCreateRequest
    {
        // Set scale for decimal precision
        if (price != null)
        {
            price = price.setScale(2, java.math.RoundingMode.HALF_UP);
        }
    }
    
    // Validate that required fields have meaningful content
    public boolean isValid()
    {
        return orderId != null
            && listingId != null
            && quantity != null && quantity >= 1
            && price != null && price.compareTo(BigDecimal.ZERO) >= 0;
    }
    
    // Calculate line total (quantity * price)
    public BigDecimal calculateLineTotal()
    {
        if (quantity != null && price != null)
        {
            return price.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }
}