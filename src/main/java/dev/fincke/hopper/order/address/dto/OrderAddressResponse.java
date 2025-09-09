package dev.fincke.hopper.order.address.dto;

import dev.fincke.hopper.order.address.OrderAddress;

import java.util.UUID;

// * DTO
// Response DTO for order address data in API responses (immutable, protects internal entity)
public record OrderAddressResponse(
    
    // Order address UUID
    UUID id,
    
    // Order ID this address belongs to
    UUID orderId,
    
    // Street address
    String street,
    
    // City name
    String city,
    
    // State/province/region
    String state,
    
    // Postal/zip code (US format)
    String postalCode
    
)
{
    
    // Static factory method to convert OrderAddress entity to response DTO
    public static OrderAddressResponse from(OrderAddress orderAddress)
    {
        if (orderAddress == null)
        {
            throw new IllegalArgumentException("OrderAddress cannot be null");
        }
        
        // Convert entity to DTO for API response
        return new OrderAddressResponse(
            orderAddress.getId(),
            orderAddress.getOrder().getId(),
            orderAddress.getStreet(),
            orderAddress.getCity(),
            orderAddress.getState(),
            orderAddress.getPostalCode()
        );
    }
}