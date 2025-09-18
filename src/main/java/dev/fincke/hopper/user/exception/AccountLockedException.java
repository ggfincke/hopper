package dev.fincke.hopper.user.exception;

import java.util.UUID;

// Domain exception for locked account access attempts
public class AccountLockedException extends RuntimeException
{
    
    // ID of the locked account
    private final UUID userId;
    private final String username;
    
    // Constructor with user ID
    public AccountLockedException(UUID userId)
    {
        super("Account with ID " + userId + " is locked");
        this.userId = userId;
        this.username = null;
    }
    
    // Constructor with username
    public AccountLockedException(String username)
    {
        super("Account '" + username + "' is locked");
        this.userId = null;
        this.username = username;
    }
    
    // Get the locked user ID
    public UUID getUserId()
    {
        return userId;
    }
    
    // Get the locked username
    public String getUsername()
    {
        return username;
    }
}