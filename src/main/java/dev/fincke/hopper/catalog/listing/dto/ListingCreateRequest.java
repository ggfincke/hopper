package dev.fincke.hopper.catalog.listing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

// Request DTO for creating a new listing (with validation constraints)
public record ListingCreateRequest(
    // product ID (must reference existing product)
    @NotNull(message = "Product ID is required")
    UUID productId,
    
    // platform ID (must reference existing platform)
    @NotNull(message = "Platform ID is required")
    UUID platformId,
    
    // external listing ID on the platform
    @NotBlank(message = "External listing ID is required")
    String externalListingId,
    
    // listing status
    @NotBlank(message = "Status is required")
    String status,
    
    // price (must be positive)
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", message = "Price must be positive")
    BigDecimal price,
    
    // quantity listed (must be non-negative)
    @Min(value = 0, message = "Quantity cannot be negative")
    int quantityListed
) {
    // * Compact Constructor
    
    // normalize and validate data on construction
    public ListingCreateRequest
    {
        externalListingId = externalListingId != null ? externalListingId.trim() : externalListingId;
        status = status != null ? status.trim() : status;
        quantityListed = Math.max(0, quantityListed);
    }
}
