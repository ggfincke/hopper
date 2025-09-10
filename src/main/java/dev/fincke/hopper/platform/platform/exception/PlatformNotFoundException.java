package dev.fincke.hopper.platform.platform.exception;

import java.util.UUID;

// Exception thrown when a requested platform cannot be found
public class PlatformNotFoundException extends RuntimeException 
{
    // * Attributes
    
    // platform ID that was not found (may be null for name-based lookups)
    private final UUID platformId;
    
    // * Constructors
    
    public PlatformNotFoundException(UUID platformId) 
    {
        super("Platform with ID " + platformId + " not found");
        this.platformId = platformId;
    }
    
    public PlatformNotFoundException(String name) 
    {
        super("Platform with name '" + name + "' not found");
        this.platformId = null;
    }
    
    public PlatformNotFoundException(UUID platformId, String message, Throwable cause) 
    {
        super(message, cause);
        this.platformId = platformId;
    }
    
    // * Getters
    
    // platform ID (may be null for name-based lookups)
    public UUID getPlatformId() 
    {
        return platformId;
    }
}