package dev.fincke.hopper.platform.credential.dto;

// Request DTO for updating an existing platform credential (all fields optional for partial updates)
public record PlatformCredentialUpdateRequest(
    // key/name of the credential
    String credentialKey,
    
    // encrypted/encoded value of the credential
    String credentialValue,
    
    // whether this credential is active
    Boolean isActive
) 
{
    // * Compact Constructor
    
    // normalize data on construction
    public PlatformCredentialUpdateRequest
    {
        credentialKey = credentialKey != null ? credentialKey.trim() : null;
        credentialValue = credentialValue != null ? credentialValue.trim() : null;
    }
    
    // * Utility Methods
    
    // check if any field provided for update
    public boolean hasUpdates() 
    {
        return credentialKey != null || credentialValue != null || isActive != null;
    }
}