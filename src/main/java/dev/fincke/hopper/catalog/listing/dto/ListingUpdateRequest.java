package dev.fincke.hopper.catalog.listing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

/**
 * Request DTO for updating an existing listing.
 * 
 * All fields are optional - only provided fields will be updated.
 * Allows partial updates without requiring all listing data.
 */
public record ListingUpdateRequest(
    // external listing ID on the platform (optional update)
    String externalListingId,
    
    // status of the listing (optional update)
    String status,
    
    // price for this listing (optional update, must be positive if provided)
    @DecimalMin(value = "0.00", message = "Price must be positive")
    BigDecimal price,
    
    // quantity listed (optional update, must be non-negative if provided)
    @Min(value = 0, message = "Quantity cannot be negative")
    Integer quantityListed
) {
    // * Compact Constructor for Data Normalization
    
    public ListingUpdateRequest
    {
        // normalize string fields by trimming whitespace (only if not null)
        externalListingId = externalListingId != null ? externalListingId.trim() : null;
        status = status != null ? status.trim() : null;
    }
    
    // * Utility Methods
    
    // checks if any field has been provided for update
    public boolean hasUpdates() 
    {
        return externalListingId != null || status != null || price != null || quantityListed != null;
    }
}