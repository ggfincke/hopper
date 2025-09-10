package dev.fincke.hopper.platform.fee.exception;

import java.math.BigDecimal;

// Exception thrown when platform fee amount violates business rules
public class InvalidFeeAmountException extends RuntimeException
{
    // * Constructors
    
    // Exception for invalid fee amount with value
    public InvalidFeeAmountException(BigDecimal amount)
    {
        super("Invalid fee amount: " + amount + ". Amount must be non-negative and within acceptable range.");
    }
    
    // Exception for invalid fee amount with specific reason
    public InvalidFeeAmountException(BigDecimal amount, String reason)
    {
        super("Invalid fee amount: " + amount + ". Reason: " + reason);
    }
    
    // Exception for invalid fee amount with custom message
    public InvalidFeeAmountException(String message)
    {
        super(message);
    }
    
    // Exception for invalid fee amount with cause
    public InvalidFeeAmountException(String message, Throwable cause)
    {
        super(message, cause);
    }
}