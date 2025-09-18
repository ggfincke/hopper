package dev.fincke.hopper.user.exception;

import java.util.UUID;

// Domain exception for user lookup failures (carries search criteria for debugging)
public class UserNotFoundException extends RuntimeException
{
    
    // Search criteria that failed (for debugging context)
    private final UUID userId;
    private final String username;
    private final String email;
    
    // Constructor for ID-based lookup failure
    public UserNotFoundException(UUID userId)
    {
        super("User with ID " + userId + " not found");
        this.userId = userId;
        this.username = null;
        this.email = null;
    }
    
    // Constructor for username-based lookup failure
    public UserNotFoundException(String identifier, boolean isUsername)
    {
        super("User with " + (isUsername ? "username" : "email") + " '" + identifier + "' not found");
        this.userId = null;
        if (isUsername)
        {
            this.username = identifier;
            this.email = null;
        }
        else
        {
            this.username = null;
            this.email = identifier;
        }
    }
    
    // Constructor for flexible identifier lookup failure
    public UserNotFoundException(String identifier)
    {
        super("User with identifier '" + identifier + "' not found");
        this.userId = null;
        this.username = identifier;
        this.email = identifier;
    }
    
    // User ID (null if lookup was by username/email)
    public UUID getUserId()
    {
        return userId;
    }
    
    // Username (null if lookup was by ID or email)
    public String getUsername()
    {
        return username;
    }
    
    // Email (null if lookup was by ID or username)
    public String getEmail()
    {
        return email;
    }
}