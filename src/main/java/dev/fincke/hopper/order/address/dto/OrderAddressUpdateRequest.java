package dev.fincke.hopper.order.address.dto;

import jakarta.validation.constraints.Pattern;

// * DTO
// Request DTO for updating existing order addresses (all fields optional for partial updates)
public record OrderAddressUpdateRequest(
    
    // Street address (optional)
    String street,
    
    // City name (optional)
    String city,
    
    // State/province/region (optional)
    String state,
    
    // Postal/zip code (optional, US ZIP code format validation if provided)
    @Pattern(regexp = "^\\d{5}(-\\d{4})?$", message = "Postal code must be a valid US ZIP code (XXXXX or XXXXX-XXXX)")
    String postalCode,

    // Country (optional)
    String country

)
{

    // Compact constructor normalizes data while preserving nulls for no-update
    public OrderAddressUpdateRequest
    {
        // Trim string fields but preserve null for no update
        street = street != null ? street.trim() : null;
        city = city != null ? city.trim() : null;
        state = state != null ? state.trim() : null;
        // Normalize postal code format (preserve null for no update)
        postalCode = postalCode != null ? postalCode.trim().replaceAll("\\s+", "") : null;
        country = country != null ? country.trim() : null;
    }
    
    // Check if at least one field is provided for update
    public boolean hasUpdates()
    {
        return street != null || city != null || state != null || postalCode != null || country != null;
    }
}
