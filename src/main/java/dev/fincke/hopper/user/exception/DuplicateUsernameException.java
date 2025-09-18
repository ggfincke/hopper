package dev.fincke.hopper.user.exception;

// Domain exception for username uniqueness violations
public class DuplicateUsernameException extends RuntimeException
{
    
    // Username that caused the conflict
    private final String username;
    
    // Constructor with conflicting username
    public DuplicateUsernameException(String username)
    {
        super("Username '" + username + "' is already taken");
        this.username = username;
    }
    
    // Get the conflicting username
    public String getUsername()
    {
        return username;
    }
}