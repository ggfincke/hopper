package dev.fincke.hopper.order.order.dto;

import dev.fincke.hopper.order.order.Order;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

// Response DTO for order data in API responses (immutable, protects internal entity)
public record OrderResponse(
    
    // Order UUID
    UUID id,
    
    // Platform information
    UUID platformId,
    String platformName,
    
    // Buyer information (optional)
    UUID buyerId,
    String buyerName,
    String buyerEmail,
    
    // External order identifier
    String externalOrderId,
    
    // Order status
    String status,
    
    // Financial information
    BigDecimal totalAmount,
    
    // Timestamps
    Timestamp orderDate
    
)
{
    
    // Static factory method to convert Order entity to response DTO
    public static OrderResponse from(Order order)
    {
        if (order == null)
        {
            throw new IllegalArgumentException("Order cannot be null");
        }
        
        // Extract buyer information if present
        UUID buyerId = null;
        String buyerName = null;
        String buyerEmail = null;
        
        if (order.getBuyer() != null)
        {
            buyerId = order.getBuyer().getId();
            buyerName = order.getBuyer().getName();
            buyerEmail = order.getBuyer().getEmail();
        }
        
        // Convert entity to DTO for API response
        return new OrderResponse(
            order.getId(),
            order.getPlatform().getId(),
            order.getPlatform().getName(),
            buyerId,
            buyerName,
            buyerEmail,
            order.getExternalOrderId(),
            order.getStatus(),
            order.getTotalAmount(),
            order.getOrderDate()
        );
    }
    
    // Static factory method with minimal platform info (for list views)
    public static OrderResponse fromMinimal(Order order)
    {
        if (order == null)
        {
            throw new IllegalArgumentException("Order cannot be null");
        }
        
        // Create minimal response for list operations
        return new OrderResponse(
            order.getId(),
            order.getPlatform().getId(),
            order.getPlatform().getName(),
            order.getBuyer() != null ? order.getBuyer().getId() : null,
            order.getBuyer() != null ? order.getBuyer().getName() : null,
            order.getBuyer() != null ? order.getBuyer().getEmail() : null,
            order.getExternalOrderId(),
            order.getStatus(),
            order.getTotalAmount(),
            order.getOrderDate()
        );
    }
    
    // Check if order has buyer assigned
    public boolean hasBuyer()
    {
        return buyerId != null;
    }
    
    // Get display name for buyer (fallback to email if no name)
    public String getBuyerDisplayName()
    {
        if (buyerName != null && !buyerName.trim().isEmpty())
        {
            return buyerName;
        }
        return buyerEmail != null ? buyerEmail : "Unknown Buyer";
    }
}