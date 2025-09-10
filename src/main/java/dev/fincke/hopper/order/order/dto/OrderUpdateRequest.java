package dev.fincke.hopper.order.order.dto;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

// Request DTO for updating existing orders (immutable, validates partial updates)
public record OrderUpdateRequest(
    
    // External order identifier (optional for updates)
    String externalOrderId,
    
    // Order status (optional for updates)
    String status,
    
    // Total amount (optional for updates)
    @DecimalMin(value = "0.00", message = "Total amount must be non-negative")
    BigDecimal totalAmount,
    
    // Order date (optional for updates)
    Timestamp orderDate,
    
    // Buyer assignment (optional, null to unassign)
    UUID buyerId
    
)
{
    
    // Custom validation for business rules
    public OrderUpdateRequest
    {
        // Trim string fields if provided
        if (externalOrderId != null)
        {
            externalOrderId = externalOrderId.trim();
        }
        
        if (status != null)
        {
            status = status.trim();
        }
        
        // Set scale for decimal precision if provided
        if (totalAmount != null)
        {
            totalAmount = totalAmount.setScale(2, java.math.RoundingMode.HALF_UP);
        }
    }
    
    // Check if any field is provided for update
    public boolean hasUpdates()
    {
        return externalOrderId != null
            || status != null
            || totalAmount != null
            || orderDate != null
            || buyerId != null;
    }
    
    // Check if external order ID should be updated
    public boolean hasExternalOrderId()
    {
        return externalOrderId != null && !externalOrderId.trim().isEmpty();
    }
    
    // Check if status should be updated
    public boolean hasStatus()
    {
        return status != null && !status.trim().isEmpty();
    }
    
    // Check if total amount should be updated
    public boolean hasTotalAmount()
    {
        return totalAmount != null;
    }
    
    // Check if order date should be updated
    public boolean hasOrderDate()
    {
        return orderDate != null;
    }
    
    // Check if buyer should be updated (including unassignment)
    public boolean hasBuyerUpdate()
    {
        return buyerId != null;
    }
}