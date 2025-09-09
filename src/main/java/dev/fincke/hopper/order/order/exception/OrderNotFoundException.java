package dev.fincke.hopper.order.order.exception;

import java.util.UUID;

// Domain exception for order lookup failures (carries search criteria for debugging)
public class OrderNotFoundException extends RuntimeException
{
    
    // Search criteria that failed (for debugging context)
    private final UUID orderId;
    private final UUID platformId;
    private final String externalOrderId;
    
    // Constructor for ID-based lookup failure
    public OrderNotFoundException(UUID orderId)
    {
        super("Order with ID " + orderId + " not found");
        this.orderId = orderId;
        this.platformId = null;
        this.externalOrderId = null;
    }
    
    // Constructor for platform and external ID lookup failure
    public OrderNotFoundException(UUID platformId, String externalOrderId)
    {
        super("Order with external ID '" + externalOrderId + "' not found for platform " + platformId);
        this.orderId = null;
        this.platformId = platformId;
        this.externalOrderId = externalOrderId;
    }
    
    // Order ID (null if lookup was not by internal ID)
    public UUID getOrderId()
    {
        return orderId;
    }
    
    // Platform ID (null if lookup was by internal ID)
    public UUID getPlatformId()
    {
        return platformId;
    }
    
    // External order ID (null if lookup was by internal ID)
    public String getExternalOrderId()
    {
        return externalOrderId;
    }
}