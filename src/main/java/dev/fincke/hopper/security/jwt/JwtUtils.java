package dev.fincke.hopper.security.jwt;

import dev.fincke.hopper.config.JwtProperties;
import dev.fincke.hopper.security.UserPrincipal;
import dev.fincke.hopper.user.RoleType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

// Utility class for JWT token generation, validation, and parsing operations
@Component
public class JwtUtils
{
    // * Attributes
    
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    
    // JWT configuration properties
    private final JwtProperties jwtProperties;
    
    // Secret key for signing tokens (derived from configuration)
    private final SecretKey secretKey;
    
    // JWT parser configured with signing key
    private final JwtParser jwtParser;
    
    // * Constructor
    
    // Constructor that initializes JWT utilities with configuration
    public JwtUtils(JwtProperties jwtProperties)
    {
        this.jwtProperties = jwtProperties;
        // Create secret key from configuration (must be at least 256 bits for HS256)
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
        // Pre-configure parser for better performance
        this.jwtParser = Jwts.parser()
            .verifyWith(secretKey)
            .requireIssuer(jwtProperties.getIssuer())
            .requireAudience(jwtProperties.getAudience())
            .build();
    }
    
    // * Token Generation Methods
    
    // Generate access token with standard expiration
    public String generateAccessToken(UserPrincipal userPrincipal)
    {
        return generateToken(userPrincipal, jwtProperties.getAccessTokenExpirationMs(), "access");
    }
    
    // Generate refresh token with extended expiration
    public String generateRefreshToken(UserPrincipal userPrincipal)
    {
        return generateToken(userPrincipal, jwtProperties.getRefreshTokenExpirationMs(), "refresh");
    }
    
    // Generate remember-me token with extended expiration
    public String generateRememberMeToken(UserPrincipal userPrincipal)
    {
        return generateToken(userPrincipal, jwtProperties.getRememberMeExpirationMs(), "access");
    }
    
    // Generate JWT token with specified expiration and type
    private String generateToken(UserPrincipal userPrincipal, long expirationMs, String tokenType)
    {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(expirationMs);
        
        // Extract role names for JWT claims
        List<String> roles = userPrincipal.getAuthorities().stream()
            .map(authority -> authority.getAuthority())
            .toList();
        
        // Build JWT with standard and custom claims
        return Jwts.builder()
            .header()
                .add("typ", "JWT")
                .and()
            .subject(userPrincipal.getUsername())
            .issuer(jwtProperties.getIssuer())
            .audience().add(jwtProperties.getAudience()).and()
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiration))
            .claim("userId", userPrincipal.getId().toString())
            .claim("email", userPrincipal.getEmail())
            .claim("roles", roles)
            .claim("tokenType", tokenType)
            .claim("enabled", userPrincipal.isEnabled())
            .claim("accountNonLocked", userPrincipal.isAccountNonLocked())
            .signWith(secretKey)
            .compact();
    }
    
    // * Token Validation Methods
    
    // Validate JWT token and return true if valid
    public boolean validateToken(String token)
    {
        try
        {
            jwtParser.parseSignedClaims(token);
            return true;
        }
        catch (SignatureException e)
        {
            logger.warn("Invalid JWT signature: {}", e.getMessage());
        }
        catch (MalformedJwtException e)
        {
            logger.warn("Malformed JWT token: {}", e.getMessage());
        }
        catch (ExpiredJwtException e)
        {
            logger.warn("Expired JWT token: {}", e.getMessage());
        }
        catch (UnsupportedJwtException e)
        {
            logger.warn("Unsupported JWT token: {}", e.getMessage());
        }
        catch (IllegalArgumentException e)
        {
            logger.warn("JWT claims string is empty: {}", e.getMessage());
        }
        catch (Exception e)
        {
            logger.error("JWT validation error: {}", e.getMessage());
        }
        return false;
    }
    
    // Validate token and check if it's an access token
    public boolean validateAccessToken(String token)
    {
        if (!validateToken(token))
        {
            return false;
        }
        
        try
        {
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            String tokenType = claims.get("tokenType", String.class);
            return "access".equals(tokenType);
        }
        catch (Exception e)
        {
            logger.warn("Error validating access token: {}", e.getMessage());
            return false;
        }
    }
    
    // Validate token and check if it's a refresh token
    public boolean validateRefreshToken(String token)
    {
        if (!validateToken(token))
        {
            return false;
        }
        
        try
        {
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            String tokenType = claims.get("tokenType", String.class);
            return "refresh".equals(tokenType);
        }
        catch (Exception e)
        {
            logger.warn("Error validating refresh token: {}", e.getMessage());
            return false;
        }
    }
    
    // * Token Parsing Methods
    
    // Extract username from valid JWT token
    public String getUsernameFromToken(String token)
    {
        try
        {
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            return claims.getSubject();
        }
        catch (Exception e)
        {
            logger.warn("Error extracting username from token: {}", e.getMessage());
            return null;
        }
    }
    
    // Extract user ID from valid JWT token
    public UUID getUserIdFromToken(String token)
    {
        try
        {
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            String userIdStr = claims.get("userId", String.class);
            return userIdStr != null ? UUID.fromString(userIdStr) : null;
        }
        catch (Exception e)
        {
            logger.warn("Error extracting user ID from token: {}", e.getMessage());
            return null;
        }
    }
    
    // Extract email from valid JWT token
    public String getEmailFromToken(String token)
    {
        try
        {
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            return claims.get("email", String.class);
        }
        catch (Exception e)
        {
            logger.warn("Error extracting email from token: {}", e.getMessage());
            return null;
        }
    }
    
    // Extract roles from valid JWT token
    @SuppressWarnings("unchecked")
    public Set<RoleType> getRolesFromToken(String token)
    {
        try
        {
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            List<String> roleNames = claims.get("roles", List.class);
            
            if (roleNames == null)
            {
                return Set.of();
            }
            
            return roleNames.stream()
                .map(RoleType::fromName)
                .collect(java.util.stream.Collectors.toSet());
        }
        catch (Exception e)
        {
            logger.warn("Error extracting roles from token: {}", e.getMessage());
            return Set.of();
        }
    }
    
    // Extract token type from valid JWT token
    public String getTokenTypeFromToken(String token)
    {
        try
        {
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            return claims.get("tokenType", String.class);
        }
        catch (Exception e)
        {
            logger.warn("Error extracting token type from token: {}", e.getMessage());
            return null;
        }
    }
    
    // Extract expiration date from valid JWT token
    public Date getExpirationFromToken(String token)
    {
        try
        {
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            return claims.getExpiration();
        }
        catch (Exception e)
        {
            logger.warn("Error extracting expiration from token: {}", e.getMessage());
            return null;
        }
    }
    
    // Check if token is expired
    public boolean isTokenExpired(String token)
    {
        Date expiration = getExpirationFromToken(token);
        return expiration != null && expiration.before(new Date());
    }
    
    // * Utility Methods
    
    // Extract Bearer token from Authorization header
    public String extractTokenFromHeader(String authHeader)
    {
        if (authHeader != null && authHeader.startsWith(jwtProperties.getTokenType() + " "))
        {
            return authHeader.substring(jwtProperties.getTokenType().length() + 1);
        }
        return null;
    }
    
    // Get remaining time until token expiration in milliseconds
    public long getTokenRemainingTime(String token)
    {
        Date expiration = getExpirationFromToken(token);
        if (expiration == null)
        {
            return 0;
        }
        return Math.max(0, expiration.getTime() - System.currentTimeMillis());
    }
    
    // Check if token needs renewal (less than 5 minutes remaining)
    public boolean needsRenewal(String token)
    {
        long remainingTime = getTokenRemainingTime(token);
        return remainingTime > 0 && remainingTime < 300000; // 5 minutes in milliseconds
    }
}