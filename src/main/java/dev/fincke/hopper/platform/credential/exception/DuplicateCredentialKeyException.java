package dev.fincke.hopper.platform.credential.exception;

import java.util.UUID;

// Exception thrown when attempting to create a credential with a duplicate platform+key combination
public class DuplicateCredentialKeyException extends RuntimeException 
{
    // * Attributes
    
    // platform ID where duplicate was attempted
    private final UUID platformId;
    
    // credential key that was duplicated
    private final String credentialKey;
    
    // * Constructors
    
    public DuplicateCredentialKeyException(UUID platformId, String credentialKey) 
    {
        super("Credential with key '" + credentialKey + "' already exists for platform " + platformId);
        this.platformId = platformId;
        this.credentialKey = credentialKey;
    }
    
    public DuplicateCredentialKeyException(UUID platformId, String credentialKey, String message, Throwable cause) 
    {
        super(message, cause);
        this.platformId = platformId;
        this.credentialKey = credentialKey;
    }
    
    // * Getters
    
    // platform ID where duplicate was attempted
    public UUID getPlatformId() 
    {
        return platformId;
    }
    
    // credential key that was duplicated
    public String getCredentialKey() 
    {
        return credentialKey;
    }
}