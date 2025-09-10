package dev.fincke.hopper.platform.fee.dto;

import dev.fincke.hopper.platform.fee.PlatformFee;

import java.math.BigDecimal;
import java.util.UUID;

// Response DTO for platform fee data (immutable for API responses)
public record PlatformFeeResponse(
    
    // platform fee ID
    UUID id,
    
    // ID of the order this fee belongs to
    UUID orderId,
    
    // external order ID for reference
    String externalOrderId,
    
    // ID of the platform for this fee
    UUID platformId,
    
    // platform name for display
    String platformName,
    
    // type of fee (transaction, processing, listing, final_value)
    String feeType,
    
    // amount of the fee
    BigDecimal amount
    
) 
{
    
    // * Static Factory Methods
    
    // create response from entity
    public static PlatformFeeResponse from(PlatformFee platformFee) 
    {
        return new PlatformFeeResponse(
            platformFee.getId(),
            platformFee.getOrder().getId(),
            platformFee.getOrder().getExternalOrderId(),
            platformFee.getOrder().getPlatform().getId(),
            platformFee.getOrder().getPlatform().getName(),
            platformFee.getFeeType(),
            platformFee.getAmount()
        );
    }
    
    // * Convenience Methods
    
    // check if this is a transaction fee
    public boolean isTransactionFee() 
    {
        return "transaction".equalsIgnoreCase(feeType);
    }
    
    // check if this is a processing fee
    public boolean isProcessingFee() 
    {
        return "processing".equalsIgnoreCase(feeType);
    }
    
    // check if this is a listing fee
    public boolean isListingFee() 
    {
        return "listing".equalsIgnoreCase(feeType);
    }
    
    // check if this is a final value fee
    public boolean isFinalValueFee() 
    {
        return "final_value".equalsIgnoreCase(feeType);
    }
    
    // get formatted amount string for display
    public String getFormattedAmount() 
    {
        return "$" + amount.toString();
    }
}