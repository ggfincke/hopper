package dev.fincke.hopper.order.buyer.dto;

import dev.fincke.hopper.order.buyer.Buyer;

import java.util.UUID;

// * DTO
// Response DTO for buyer data in API responses (immutable, protects internal entity)
public record BuyerResponse(
    
    // Buyer UUID
    UUID id,
    
    // Email address
    String email,
    
    // Display name
    String name
    
)
{
    
    // Static factory method to convert Buyer entity to response DTO
    public static BuyerResponse from(Buyer buyer)
    {
        if (buyer == null)
        {
            throw new IllegalArgumentException("Buyer cannot be null");
        }
        
        // Convert entity to DTO for API response
        return new BuyerResponse(
            buyer.getId(),
            buyer.getEmail(),
            buyer.getName()
        );
    }
}