package dev.fincke.hopper.order.buyer.exception;

// Business rule exception for duplicate email addresses (carries email for context)
public class DuplicateEmailException extends RuntimeException
{
    
    // The duplicate email that caused the conflict
    private final String email;
    
    // Constructor with the conflicting email address
    public DuplicateEmailException(String email)
    {
        super("Buyer with email '" + email + "' already exists");
        this.email = email;
    }
    
    // Email that caused the conflict
    public String getEmail()
    {
        return email;
    }
}