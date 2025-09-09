package dev.fincke.hopper.catalog.listing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

// Request DTO for updating an existing listing (all fields optional for partial updates)
public record ListingUpdateRequest(
    // external listing ID on the platform
    String externalListingId,
    
    // listing status
    String status,
    
    // price (must be positive if provided)
    @DecimalMin(value = "0.00", message = "Price must be positive")
    BigDecimal price,
    
    // quantity listed (must be non-negative if provided)
    @Min(value = 0, message = "Quantity cannot be negative")
    Integer quantityListed
) {
    // * Compact Constructor
    
    // normalize data on construction
    public ListingUpdateRequest
    {
        externalListingId = externalListingId != null ? externalListingId.trim() : null;
        status = status != null ? status.trim() : null;
    }
    
    // * Utility Methods
    
    // check if any field provided for update
    public boolean hasUpdates() 
    {
        return externalListingId != null || status != null || price != null || quantityListed != null;
    }
}