package dev.fincke.hopper.platform.fee.dto;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.util.UUID;

// Request DTO for updating platform fees (partial updates with optional fields)
public record PlatformFeeUpdateRequest(
    
    // optional: new order ID for fee association
    UUID orderId,
    
    // optional: new fee type
    String feeType,
    
    // optional: new amount (must be non-negative if provided)
    @DecimalMin(value = "0.00", message = "Fee amount must be non-negative")
    BigDecimal amount
    
) 
{
    
    // * Update Detection Methods
    
    // check if at least one field is provided for update
    public boolean hasUpdates() 
    {
        return orderId != null || feeType != null || amount != null;
    }
    
    // check if order ID update is requested
    public boolean hasOrderId() 
    {
        return orderId != null;
    }
    
    // check if fee type update is requested
    public boolean hasFeeType() 
    {
        return feeType != null && !feeType.trim().isEmpty();
    }
    
    // check if amount update is requested
    public boolean hasAmount() 
    {
        return amount != null;
    }
    
    // * Value Processing Methods
    
    // get trimmed fee type for processing (null-safe)
    public String trimmedFeeType() 
    {
        return feeType != null ? feeType.trim() : null;
    }
    
    // normalize fee type to lowercase for consistency (null-safe)
    public String normalizedFeeType() 
    {
        return feeType != null ? feeType.toLowerCase().trim() : null;
    }
    
    // validate amount is positive (if provided)
    public boolean isValidAmount() 
    {
        return amount == null || amount.compareTo(BigDecimal.ZERO) >= 0;
    }
}