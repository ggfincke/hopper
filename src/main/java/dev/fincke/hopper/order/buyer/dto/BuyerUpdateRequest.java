package dev.fincke.hopper.order.buyer.dto;

import jakarta.validation.constraints.Email;

// * DTO
// Request DTO for updating existing buyers (all fields optional for partial updates)
public record BuyerUpdateRequest(
    
    // Email address (optional, validated with @Email if provided)
    @Email(message = "Email must be a valid email address")
    String email,
    
    // Buyer name (optional)
    String name
    
)
{
    
    // Compact constructor normalizes data while preserving nulls
    public BuyerUpdateRequest
    {
        // Normalize email to lowercase (preserve null for no update)
        email = email != null ? email.trim().toLowerCase() : null;
        // Trim name whitespace (preserve null for no update)
        name = name != null ? name.trim() : null;
    }
    
    // Check if at least one field is provided for update
    public boolean hasUpdates()
    {
        return email != null || name != null;
    }
}