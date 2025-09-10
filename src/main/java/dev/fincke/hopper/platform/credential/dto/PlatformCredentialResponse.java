package dev.fincke.hopper.platform.credential.dto;

import dev.fincke.hopper.platform.credential.PlatformCredential;

import java.util.UUID;

// Response DTO for platform credential data (immutable for API responses)
public record PlatformCredentialResponse(
    // credential ID
    UUID id,
    
    // platform ID where credential belongs
    UUID platformId,
    
    // name of platform for display purposes
    String platformName,
    
    // key/name of the credential
    String credentialKey,
    
    // credential value (always redacted for security)
    String credentialValue,
    
    // whether this credential is active
    Boolean isActive
) 
{
    // * Static Factory Methods
    
    // create response from entity (with credential value redacted)
    public static PlatformCredentialResponse from(PlatformCredential credential) 
    {
        return new PlatformCredentialResponse(
            credential.getId(),
            credential.getPlatform().getId(),
            credential.getPlatform().getName(),
            credential.getCredentialKey(),
            "***REDACTED***", // never expose credential values in API responses
            credential.getIsActive()
        );
    }
}