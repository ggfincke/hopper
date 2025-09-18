package dev.fincke.hopper.user.exception;

// Domain exception for email uniqueness violations
public class DuplicateUserEmailException extends RuntimeException
{
    
    // Email that caused the conflict
    private final String email;
    
    // Constructor with conflicting email
    public DuplicateUserEmailException(String email)
    {
        super("Email '" + email + "' is already registered");
        this.email = email;
    }
    
    // Get the conflicting email
    public String getEmail()
    {
        return email;
    }
}