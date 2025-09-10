package dev.fincke.hopper.platform.fee.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

// Request DTO for creating platform fees (immutable for thread safety)
public record PlatformFeeCreateRequest(
    
    // ID of the order this fee belongs to
    @NotNull(message = "Order ID is required")
    UUID orderId,
    
    // type of fee (transaction, processing, listing, final_value)
    @NotBlank(message = "Fee type is required")
    String feeType,
    
    // amount of the fee (must be non-negative)
    @NotNull(message = "Fee amount is required")
    @DecimalMin(value = "0.00", message = "Fee amount must be non-negative")
    BigDecimal amount
    
) 
{
    
    // * Validation Methods
    
    // check if all required fields are present and valid
    public boolean isValid() 
    {
        return orderId != null &&
               feeType != null && !feeType.trim().isEmpty() &&
               amount != null && amount.compareTo(BigDecimal.ZERO) >= 0;
    }
    
    // normalize fee type to lowercase for consistency
    public String normalizedFeeType() 
    {
        return feeType != null ? feeType.toLowerCase().trim() : null;
    }
    
    // get trimmed fee type for processing
    public String trimmedFeeType() 
    {
        return feeType != null ? feeType.trim() : null;
    }
}