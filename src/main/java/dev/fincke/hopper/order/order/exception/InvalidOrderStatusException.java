package dev.fincke.hopper.order.order.exception;

import dev.fincke.hopper.api.error.BadRequestException;

import java.util.UUID;

// Domain exception for invalid order status transitions (business rule violation)
public class InvalidOrderStatusException extends BadRequestException
{
    
    // Context information for debugging and error handling
    private final UUID orderId;
    private final String currentStatus;
    private final String requestedStatus;
    
    // Constructor for status transition validation failure
    public InvalidOrderStatusException(UUID orderId, String currentStatus, String requestedStatus)
    {
        super(String.format(
            "Invalid status transition for order %s: cannot change from '%s' to '%s'",
            orderId, currentStatus, requestedStatus
        ));
        this.orderId = orderId;
        this.currentStatus = currentStatus;
        this.requestedStatus = requestedStatus;
    }
    
    // Constructor for invalid status value
    public InvalidOrderStatusException(String invalidStatus)
    {
        super("Invalid order status: '" + invalidStatus + "'");
        this.orderId = null;
        this.currentStatus = null;
        this.requestedStatus = invalidStatus;
    }
    
    // Order ID (null for general status validation)
    public UUID getOrderId()
    {
        return orderId;
    }
    
    // Current status (null for general status validation)
    public String getCurrentStatus()
    {
        return currentStatus;
    }
    
    // Requested status that caused the error
    public String getRequestedStatus()
    {
        return requestedStatus;
    }
}