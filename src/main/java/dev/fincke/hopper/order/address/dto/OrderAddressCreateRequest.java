package dev.fincke.hopper.order.address.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

// * DTO
// Request DTO for creating new order addresses with validation and normalization
public record OrderAddressCreateRequest(
    
    // Order ID this address belongs to (required)
    @NotNull(message = "Order ID is required")
    UUID orderId,
    
    // Street address (required, validated with @NotBlank)
    @NotBlank(message = "Street address is required")
    String street,
    
    // City name (required, validated with @NotBlank)
    @NotBlank(message = "City is required") 
    String city,
    
    // State/province/region (required, validated with @NotBlank)
    @NotBlank(message = "State is required")
    String state,
    
    // Postal/zip code (required, US ZIP code format validation)
    @NotBlank(message = "Postal code is required")
    @Pattern(regexp = "^\\d{5}(-\\d{4})?$", message = "Postal code must be a valid US ZIP code (XXXXX or XXXXX-XXXX)")
    String postalCode
    
)
{
    
    // Compact constructor normalizes data (trims whitespace, standardizes formats)
    public OrderAddressCreateRequest
    {
        // Trim all string fields for consistency
        street = street != null ? street.trim() : street;
        city = city != null ? city.trim() : city;
        state = state != null ? state.trim() : state;
        // Remove extra spaces and normalize postal code format
        postalCode = postalCode != null ? postalCode.trim().replaceAll("\\s+", "") : postalCode;
    }
}