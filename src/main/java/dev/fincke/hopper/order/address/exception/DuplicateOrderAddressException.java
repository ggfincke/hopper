package dev.fincke.hopper.order.address.exception;

import java.util.UUID;

// Business rule exception for duplicate address per order (carries order ID for context)
public class DuplicateOrderAddressException extends RuntimeException
{
    
    // The order ID that already has an address
    private final UUID orderId;
    
    // Constructor with the order that already has an address
    public DuplicateOrderAddressException(UUID orderId)
    {
        super("Order with ID " + orderId + " already has an address");
        this.orderId = orderId;
    }
    
    // orderId that caused the conflict
    public UUID getOrderId()
    {
        return orderId;
    }
}