package dev.fincke.hopper.platform.fee.exception;

import java.util.UUID;

// Exception thrown when attempting to create duplicate fee type for an order
public class DuplicateFeeTypeException extends RuntimeException
{
    // * Constructors
    
    // Exception for duplicate fee type on order
    public DuplicateFeeTypeException(UUID orderId, String feeType)
    {
        super("Fee type '" + feeType + "' already exists for order: " + orderId);
    }
    
    // Exception for duplicate fee type with custom message
    public DuplicateFeeTypeException(String message)
    {
        super(message);
    }
    
    // Exception for duplicate fee type with cause
    public DuplicateFeeTypeException(String message, Throwable cause)
    {
        super(message, cause);
    }
}