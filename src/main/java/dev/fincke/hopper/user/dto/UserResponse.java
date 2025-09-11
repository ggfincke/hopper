package dev.fincke.hopper.user.dto;

import dev.fincke.hopper.user.Role;
import dev.fincke.hopper.user.RoleType;
import dev.fincke.hopper.user.User;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

// Response DTO for user data in API responses (immutable, protects internal entity, excludes password)
public record UserResponse(
    
    // User UUID
    UUID id,
    
    // Username
    String username,
    
    // Email address
    String email,
    
    // Account enabled status
    boolean enabled,
    
    // Account locked status
    boolean accountLocked,
    
    // Failed login attempts
    int failedLoginAttempts,
    
    // User roles (as enum types for API clarity)
    Set<RoleType> roles,
    
    // Account creation timestamp
    LocalDateTime createdAt,
    
    // Last update timestamp
    LocalDateTime updatedAt
    
)
{
    
    // Static factory method to convert User entity to response DTO
    public static UserResponse from(User user)
    {
        if (user == null)
        {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        // Extract role types from role entities for cleaner API response
        Set<RoleType> roleTypes = user.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toSet());
        
        // Convert entity to DTO for API response (password excluded for security)
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.isEnabled(),
            user.isAccountLocked(),
            user.getFailedLoginAttempts(),
            roleTypes,
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
    
    // Static factory method for minimal user info (username and roles only)
    public static UserResponse minimal(User user)
    {
        if (user == null)
        {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        Set<RoleType> roleTypes = user.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toSet());
        
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            null, // email excluded for privacy
            user.isEnabled(),
            user.isAccountLocked(),
            0, // failed attempts excluded for privacy
            roleTypes,
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}