package dev.fincke.hopper.order.item.dto;

import dev.fincke.hopper.order.item.OrderItem;

import java.math.BigDecimal;
import java.util.UUID;

// Response DTO for order item data (immutable for API responses)
public record OrderItemResponse(
    // order item ID
    UUID id,
    
    // ID of the order this item belongs to
    UUID orderId,
    
    // ID of the listing this item references
    UUID listingId,
    
    // external listing ID for reference
    String externalListingId,
    
    // quantity purchased
    int quantity,
    
    // unit price at time of sale
    BigDecimal price,
    
    // calculated line total (quantity * price)
    BigDecimal lineTotal
) {
    // * Static Factory Methods
    
    // create response from entity
    public static OrderItemResponse from(OrderItem orderItem) 
    {
        // calculate line total for display
        BigDecimal lineTotal = orderItem.getPrice()
            .multiply(BigDecimal.valueOf(orderItem.getQuantity()));
        
        return new OrderItemResponse(
            orderItem.getId(),
            orderItem.getOrder().getId(),
            orderItem.getListing().getId(),
            orderItem.getListing().getExternalListingId(),
            orderItem.getQuantity(),
            orderItem.getPrice(),
            lineTotal
        );
    }
}