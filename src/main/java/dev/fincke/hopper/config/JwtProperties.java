package dev.fincke.hopper.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

// Configuration properties for JWT token settings (mapped from application.properties)
@Configuration
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties
{
    // * Attributes
    
    // Base64-encoded secret key for signing JWTs (must be at least 256 bits)
    private String secret = "defaultSecretKeyThatShouldBeOverriddenInProduction";
    
    // Token issuer name (typically the application name)
    private String issuer = "hopper-api";
    
    // Token audience (intended recipients of the token)
    private String audience = "hopper-users";
    
    // Access token expiration time (shorter for security)
    private Duration accessTokenExpiration = Duration.ofMinutes(15);
    
    // Refresh token expiration time (longer for convenience)
    private Duration refreshTokenExpiration = Duration.ofDays(7);
    
    // Token type for authorization header
    private String tokenType = "Bearer";
    
    // Header name for JWT tokens
    private String headerName = "Authorization";
    
    // Cookie name for refresh tokens (when using HTTP-only cookies)
    private String refreshTokenCookieName = "refresh-token";
    
    // Remember me token expiration (extended duration)
    private Duration rememberMeExpiration = Duration.ofDays(30);
    
    // * Getters and Setters
    
    // Secret key for JWT signing
    public String getSecret()
    {
        return secret;
    }
    
    // Secret key for JWT signing
    public void setSecret(String secret)
    {
        this.secret = secret;
    }
    
    // Token issuer
    public String getIssuer()
    {
        return issuer;
    }
    
    // Token issuer
    public void setIssuer(String issuer)
    {
        this.issuer = issuer;
    }
    
    // Token audience
    public String getAudience()
    {
        return audience;
    }
    
    // Token audience
    public void setAudience(String audience)
    {
        this.audience = audience;
    }
    
    // Access token expiration duration
    public Duration getAccessTokenExpiration()
    {
        return accessTokenExpiration;
    }
    
    // Access token expiration duration
    public void setAccessTokenExpiration(Duration accessTokenExpiration)
    {
        this.accessTokenExpiration = accessTokenExpiration;
    }
    
    // Refresh token expiration duration
    public Duration getRefreshTokenExpiration()
    {
        return refreshTokenExpiration;
    }
    
    // Refresh token expiration duration
    public void setRefreshTokenExpiration(Duration refreshTokenExpiration)
    {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }
    
    // Authorization header token type
    public String getTokenType()
    {
        return tokenType;
    }
    
    // Authorization header token type
    public void setTokenType(String tokenType)
    {
        this.tokenType = tokenType;
    }
    
    // Authorization header name
    public String getHeaderName()
    {
        return headerName;
    }
    
    // Authorization header name
    public void setHeaderName(String headerName)
    {
        this.headerName = headerName;
    }
    
    // Refresh token cookie name
    public String getRefreshTokenCookieName()
    {
        return refreshTokenCookieName;
    }
    
    // Refresh token cookie name
    public void setRefreshTokenCookieName(String refreshTokenCookieName)
    {
        this.refreshTokenCookieName = refreshTokenCookieName;
    }
    
    // Remember me token expiration duration
    public Duration getRememberMeExpiration()
    {
        return rememberMeExpiration;
    }
    
    // Remember me token expiration duration
    public void setRememberMeExpiration(Duration rememberMeExpiration)
    {
        this.rememberMeExpiration = rememberMeExpiration;
    }
    
    // * Utility Methods
    
    // Get access token expiration in milliseconds
    public long getAccessTokenExpirationMs()
    {
        return accessTokenExpiration.toMillis();
    }
    
    // Get refresh token expiration in milliseconds
    public long getRefreshTokenExpirationMs()
    {
        return refreshTokenExpiration.toMillis();
    }
    
    // Get remember me expiration in milliseconds
    public long getRememberMeExpirationMs()
    {
        return rememberMeExpiration.toMillis();
    }
    
    // Check if token type is Bearer
    public boolean isBearerToken()
    {
        return "Bearer".equalsIgnoreCase(tokenType);
    }
}