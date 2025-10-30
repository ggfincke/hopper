package dev.fincke.hopper.order.item.exception;

import dev.fincke.hopper.api.error.NotFoundException;

import java.util.UUID;

// Exception thrown when order item cannot be found by ID
public class OrderItemNotFoundException extends NotFoundException
{
    // * Constructors
    
    // Exception for order item not found by ID
    public OrderItemNotFoundException(UUID id)
    {
        super("Order item not found with ID: " + id);
    }
    
    // Exception for order item not found with custom message
    public OrderItemNotFoundException(String message)
    {
        super(message);
    }
    
    // Exception for order item not found with cause
    public OrderItemNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }
}