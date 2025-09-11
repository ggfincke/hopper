package dev.fincke.hopper.auth.dto;

import jakarta.validation.constraints.NotBlank;

// Request DTO for refreshing JWT access tokens
public record RefreshTokenRequest(
    
    // Refresh token used to generate new access token
    @NotBlank(message = "Refresh token is required")
    String refreshToken
    
)
{
    
    // Trim whitespace from refresh token
    public String getTrimmedRefreshToken()
    {
        return refreshToken != null ? refreshToken.trim() : null;
    }
    
    // Check if refresh token is provided and not empty
    public boolean hasValidToken()
    {
        return refreshToken != null && !refreshToken.trim().isEmpty();
    }
}