package dev.fincke.hopper.platform.fee.exception;

import java.util.UUID;

// Exception thrown when platform fee cannot be found by ID
public class PlatformFeeNotFoundException extends RuntimeException
{
    // * Constructors
    
    // Exception for platform fee not found by ID
    public PlatformFeeNotFoundException(UUID id)
    {
        super("Platform fee not found with ID: " + id);
    }
    
    // Exception for platform fee not found with custom message
    public PlatformFeeNotFoundException(String message)
    {
        super(message);
    }
    
    // Exception for platform fee not found with cause
    public PlatformFeeNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }
}