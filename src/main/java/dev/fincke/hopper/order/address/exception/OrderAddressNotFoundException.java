package dev.fincke.hopper.order.address.exception;

import dev.fincke.hopper.api.error.NotFoundException;

import java.util.UUID;

// Domain exception for order address lookup failures (carries search criteria for debugging)
public class OrderAddressNotFoundException extends NotFoundException
{
    
    // Search criteria that failed (for debugging context)
    private final UUID addressId;
    private final UUID orderId;
    
    // Constructor for ID-based lookup failure
    public OrderAddressNotFoundException(UUID addressId)
    {
        super("Order address with ID " + addressId + " not found");
        this.addressId = addressId;
        this.orderId = null;
    }
    
    // Constructor for order-based lookup failure  
    public OrderAddressNotFoundException(String type, UUID orderId)
    {
        super("Order address for order ID " + orderId + " not found");
        this.addressId = null;
        this.orderId = orderId;
    }
    
    // addressId (null if lookup was by order)
    public UUID getAddressId()
    {
        return addressId;
    }
    
    // orderId (null if lookup was by address ID)
    public UUID getOrderId()
    {
        return orderId;
    }
}