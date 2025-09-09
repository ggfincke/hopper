package dev.fincke.hopper.catalog.listing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request DTO for creating a new listing.
 * 
 * Contains all required and optional fields for listing creation with
 * validation constraints to ensure data integrity before business logic.
 */
public record ListingCreateRequest(
    // product ID (required, must reference existing product)
    @NotNull(message = "Product ID is required")
    UUID productId,
    
    // platform ID (required, must reference existing platform)
    @NotNull(message = "Platform ID is required")
    UUID platformId,
    
    // external listing ID on the platform (required)
    @NotBlank(message = "External listing ID is required")
    String externalListingId,
    
    // status of the listing (required)
    @NotBlank(message = "Status is required")
    String status,
    
    // price for this listing (required, must be positive)
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", message = "Price must be positive")
    BigDecimal price,
    
    // quantity listed (must be non-negative)
    @Min(value = 0, message = "Quantity cannot be negative")
    int quantityListed
) {
    // * Compact Constructor for Data Normalization
    
    public ListingCreateRequest
    {
        // normalize string fields by trimming whitespace
        externalListingId = externalListingId != null ? externalListingId.trim() : externalListingId;
        status = status != null ? status.trim() : status;
        
        // ensure quantity defaults to 0 if not provided
        quantityListed = Math.max(0, quantityListed);
    }
}
