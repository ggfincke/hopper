package dev.fincke.hopper.platform.credential.exception;

import java.util.UUID;

// Exception thrown when a requested platform credential cannot be found
public class PlatformCredentialNotFoundException extends RuntimeException 
{
    // * Attributes
    
    // credential ID that was not found (may be null for key-based lookups)
    private final UUID credentialId;
    
    // * Constructors
    
    public PlatformCredentialNotFoundException(UUID credentialId) 
    {
        super("Platform credential with ID " + credentialId + " not found");
        this.credentialId = credentialId;
    }
    
    public PlatformCredentialNotFoundException(UUID platformId, String credentialKey) 
    {
        super("Platform credential with key '" + credentialKey + "' not found for platform " + platformId);
        this.credentialId = null;
    }
    
    public PlatformCredentialNotFoundException(UUID credentialId, String message, Throwable cause) 
    {
        super(message, cause);
        this.credentialId = credentialId;
    }
    
    // * Getters
    
    // credential ID (may be null for key-based lookups)
    public UUID getCredentialId() 
    {
        return credentialId;
    }
}