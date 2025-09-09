package dev.fincke.hopper.order.order.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

// Request DTO for creating new orders (immutable, validates business rules)
public record OrderCreateRequest(
    
    // Platform ID where order originates (required)
    @NotNull(message = "Platform ID is required")
    UUID platformId,
    
    // External order identifier from platform (required, unique per platform)
    @NotBlank(message = "External order ID is required")
    String externalOrderId,
    
    // Initial order status (required, default: "pending")
    @NotBlank(message = "Status is required")
    String status,
    
    // Total amount for order (required, minimum 0.00)
    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.00", message = "Total amount must be non-negative")
    BigDecimal totalAmount,
    
    // Date when order was placed (required)
    @NotNull(message = "Order date is required")
    Timestamp orderDate,
    
    // Optional buyer ID (can be assigned later)
    UUID buyerId
    
)
{
    
    // Custom validation for business rules
    public OrderCreateRequest
    {
        // Trim string fields
        if (externalOrderId != null)
        {
            externalOrderId = externalOrderId.trim();
        }
        
        if (status != null)
        {
            status = status.trim();
        }
        
        // Set scale for decimal precision
        if (totalAmount != null)
        {
            totalAmount = totalAmount.setScale(2, java.math.RoundingMode.HALF_UP);
        }
    }
    
    // Check if buyer is assigned
    public boolean hasBuyer()
    {
        return buyerId != null;
    }
    
    // Validate that required fields have meaningful content
    public boolean isValid()
    {
        return platformId != null
            && externalOrderId != null && !externalOrderId.trim().isEmpty()
            && status != null && !status.trim().isEmpty()
            && totalAmount != null && totalAmount.compareTo(BigDecimal.ZERO) >= 0
            && orderDate != null;
    }
}