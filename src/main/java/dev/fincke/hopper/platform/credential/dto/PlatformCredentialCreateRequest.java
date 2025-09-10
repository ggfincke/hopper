package dev.fincke.hopper.platform.credential.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

// Request DTO for creating a new platform credential (with validation constraints)
public record PlatformCredentialCreateRequest(
    // platform ID where credential belongs (required)
    @NotNull(message = "Platform ID is required")
    UUID platformId,
    
    // key/name of the credential (required and unique per platform)
    @NotBlank(message = "Credential key is required")
    String credentialKey,
    
    // encrypted/encoded value of the credential (required)
    @NotBlank(message = "Credential value is required")
    String credentialValue,
    
    // whether this credential is active (defaults to true if null)
    Boolean isActive
) 
{
    // * Compact Constructor
    
    // normalize data on construction and set default values
    public PlatformCredentialCreateRequest
    {
        credentialKey = credentialKey != null ? credentialKey.trim() : credentialKey;
        credentialValue = credentialValue != null ? credentialValue.trim() : credentialValue;
        isActive = isActive != null ? isActive : true;
    }
}