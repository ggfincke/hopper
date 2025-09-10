package dev.fincke.hopper.order.item.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

// Request DTO for updating existing order items (all fields optional)
public record OrderItemUpdateRequest(
    
    // New quantity for the order item (optional, minimum 1 if provided)
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity,
    
    // New unit price for the order item (optional, minimum 0.00 if provided)
    @DecimalMin(value = "0.00", message = "Price must be non-negative")
    BigDecimal price
    
)
{
    
    // Custom validation for business rules
    public OrderItemUpdateRequest
    {
        // Set scale for decimal precision
        if (price != null)
        {
            price = price.setScale(2, java.math.RoundingMode.HALF_UP);
        }
    }
    
    // Check if at least one field is provided for update
    public boolean hasUpdates()
    {
        return quantity != null || price != null;
    }
    
    // Check if quantity update is provided
    public boolean hasQuantity()
    {
        return quantity != null;
    }
    
    // Check if price update is provided
    public boolean hasPrice()
    {
        return price != null;
    }
    
    // Validate update data
    public boolean isValid()
    {
        // At least one field must be provided
        if (!hasUpdates())
        {
            return false;
        }
        
        // Validate quantity if provided
        if (quantity != null && quantity < 1)
        {
            return false;
        }
        
        // Validate price if provided
        if (price != null && price.compareTo(BigDecimal.ZERO) < 0)
        {
            return false;
        }
        
        return true;
    }
}