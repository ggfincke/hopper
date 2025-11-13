package dev.fincke.hopper.user.exception;

import dev.fincke.hopper.api.error.BadRequestException;

// Domain exception for password validation failures
public class InvalidPasswordException extends BadRequestException
{
    
    // Validation error details
    private final String reason;
    
    // Constructor with validation reason
    public InvalidPasswordException(String reason)
    {
        super("Password validation failed: " + reason);
        this.reason = reason;
    }
    
    // Get the validation failure reason
    public String getReason()
    {
        return reason;
    }
}