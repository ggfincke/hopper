package dev.fincke.hopper.auth.dto;

import dev.fincke.hopper.user.RoleType;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

// Response DTO for token validation operations
public record TokenValidationResponse(
    
    // Whether the token is valid
    boolean valid,
    
    // Token expiration time (if valid)
    LocalDateTime expiresAt,
    
    // Time remaining until expiration in seconds (if valid)
    long remainingTimeSeconds,
    
    // User information from token (if valid)
    TokenUserInfo user,
    
    // Error message (if invalid)
    String errorMessage,
    
    // Error code for specific validation failures
    String errorCode
    
)
{
    
    // Nested record for user information in token validation
    public record TokenUserInfo(
        UUID id,
        String username,
        String email,
        Set<RoleType> roles,
        boolean enabled,
        boolean accountLocked
    ) {}
    
    // Factory method for valid token response
    public static TokenValidationResponse valid(LocalDateTime expiresAt,
                                              long remainingTimeSeconds,
                                              UUID userId,
                                              String username,
                                              String email,
                                              Set<RoleType> roles,
                                              boolean enabled,
                                              boolean accountLocked)
    {
        TokenUserInfo userInfo = new TokenUserInfo(
            userId, username, email, roles, enabled, accountLocked
        );
        
        return new TokenValidationResponse(
            true,
            expiresAt,
            remainingTimeSeconds,
            userInfo,
            null,
            null
        );
    }
    
    // Factory method for invalid token response
    public static TokenValidationResponse invalid(String errorMessage, String errorCode)
    {
        return new TokenValidationResponse(
            false,
            null,
            0,
            null,
            errorMessage,
            errorCode
        );
    }
    
    // Factory method for expired token
    public static TokenValidationResponse expired()
    {
        return invalid("Token has expired", "TOKEN_EXPIRED");
    }
    
    // Factory method for malformed token
    public static TokenValidationResponse malformed()
    {
        return invalid("Token is malformed or invalid", "TOKEN_MALFORMED");
    }
    
    // Factory method for missing token
    public static TokenValidationResponse missing()
    {
        return invalid("Token is missing", "TOKEN_MISSING");
    }
    
    // Factory method for user not found
    public static TokenValidationResponse userNotFound()
    {
        return invalid("User associated with token not found", "USER_NOT_FOUND");
    }
    
    // Factory method for account disabled
    public static TokenValidationResponse accountDisabled()
    {
        return invalid("User account is disabled", "ACCOUNT_DISABLED");
    }
    
    // Factory method for account locked
    public static TokenValidationResponse accountLocked()
    {
        return invalid("User account is locked", "ACCOUNT_LOCKED");
    }
    
    // Check if token is near expiration (less than 5 minutes)
    public boolean isNearExpiration()
    {
        return valid && remainingTimeSeconds > 0 && remainingTimeSeconds < 300; // 5 minutes
    }
    
    // Check if token needs immediate renewal
    public boolean needsRenewal()
    {
        return valid && remainingTimeSeconds > 0 && remainingTimeSeconds < 60; // 1 minute
    }
    
    // Get user ID if available
    public UUID getUserId()
    {
        return user != null ? user.id() : null;
    }
    
    // Get username if available
    public String getUsername()
    {
        return user != null ? user.username() : null;
    }
    
    // Check if user has specific role
    public boolean hasRole(RoleType roleType)
    {
        return user != null && user.roles() != null && user.roles().contains(roleType);
    }
    
    // Check if user is admin
    public boolean isAdmin()
    {
        return hasRole(RoleType.ADMIN);
    }
}