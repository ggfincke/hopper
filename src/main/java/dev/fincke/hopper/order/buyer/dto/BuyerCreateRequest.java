package dev.fincke.hopper.order.buyer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// * DTO
// Request DTO for creating new buyers with validation and normalization
public record BuyerCreateRequest(
    
    // Email address (validated with @Email annotation)
    @Email(message = "Email must be a valid email address")
    String email,
    
    // Buyer name (required, validated with @NotBlank)
    @NotBlank(message = "Buyer name is required")
    String name
    
)
{
    
    // Compact constructor normalizes data (email to lowercase, trims whitespace)
    public BuyerCreateRequest
    {
        // Normalize email to lowercase for consistency
        email = email != null ? email.trim().toLowerCase() : null;
        // Trim whitespace from name
        name = name != null ? name.trim() : name;
    }
}