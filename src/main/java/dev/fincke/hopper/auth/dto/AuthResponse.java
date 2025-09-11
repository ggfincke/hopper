package dev.fincke.hopper.auth.dto;

import dev.fincke.hopper.user.RoleType;
import dev.fincke.hopper.user.dto.UserResponse;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

// Response DTO for authentication operations (login and token refresh)
public record AuthResponse(
    
    // Access token for API authentication (short-lived)
    String accessToken,
    
    // Refresh token for obtaining new access tokens (long-lived)
    String refreshToken,
    
    // Token type (typically "Bearer")
    String tokenType,
    
    // Access token expiration time in seconds
    long expiresIn,
    
    // Refresh token expiration time in seconds
    long refreshExpiresIn,
    
    // Authenticated user information
    UserInfo user
    
)
{
    
    // Nested record for user information in auth response
    public record UserInfo(
        UUID id,
        String username,
        String email,
        Set<RoleType> roles,
        boolean enabled,
        boolean accountLocked,
        LocalDateTime lastLogin
    )
    {
        // Create UserInfo from UserResponse
        public static UserInfo from(UserResponse userResponse)
        {
            return new UserInfo(
                userResponse.id(),
                userResponse.username(),
                userResponse.email(),
                userResponse.roles(),
                userResponse.enabled(),
                userResponse.accountLocked(),
                LocalDateTime.now() // Current time as last login
            );
        }
    }
    
    // Factory method for successful authentication
    public static AuthResponse success(String accessToken, 
                                     String refreshToken, 
                                     String tokenType,
                                     long accessExpiresIn,
                                     long refreshExpiresIn,
                                     UserResponse user)
    {
        return new AuthResponse(
            accessToken,
            refreshToken,
            tokenType,
            accessExpiresIn,
            refreshExpiresIn,
            UserInfo.from(user)
        );
    }
    
    // Factory method for token refresh (no user info update needed)
    public static AuthResponse refreshSuccess(String accessToken,
                                            String refreshToken,
                                            String tokenType,
                                            long accessExpiresIn,
                                            long refreshExpiresIn,
                                            UserInfo existingUserInfo)
    {
        return new AuthResponse(
            accessToken,
            refreshToken,
            tokenType,
            accessExpiresIn,
            refreshExpiresIn,
            existingUserInfo
        );
    }
    
    // Check if response contains valid tokens
    public boolean hasValidTokens()
    {
        return accessToken != null && !accessToken.trim().isEmpty() &&
               refreshToken != null && !refreshToken.trim().isEmpty();
    }
    
    // Check if access token is near expiration (less than 5 minutes)
    public boolean isAccessTokenNearExpiry()
    {
        return expiresIn > 0 && expiresIn < 300; // 5 minutes in seconds
    }
    
    // Get access token with type prefix for Authorization header
    public String getAuthorizationHeader()
    {
        return tokenType + " " + accessToken;
    }
}