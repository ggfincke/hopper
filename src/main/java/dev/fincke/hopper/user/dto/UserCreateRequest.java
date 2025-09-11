package dev.fincke.hopper.user.dto;

import dev.fincke.hopper.user.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

// Request DTO for creating new users with validation and normalization
public record UserCreateRequest(
    
    // Username (required, 3-50 characters)
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    String username,
    
    // Email address (required, validated format)
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    String email,
    
    // Password (required, minimum 8 characters for security)
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password,
    
    // User roles (optional, defaults to USER if not provided)
    Set<RoleType> roles,
    
    // Account enabled status (optional, defaults to true)
    Boolean enabled
    
)
{
    
    // Compact constructor normalizes data and applies defaults
    public UserCreateRequest
    {
        // Normalize username by trimming whitespace
        username = username != null ? username.trim() : username;
        // Normalize email to lowercase for consistency
        email = email != null ? email.trim().toLowerCase() : email;
        // Password is kept as-is (will be encrypted in service layer)
        // Default to USER role if no roles specified
        roles = (roles == null || roles.isEmpty()) ? Set.of(RoleType.USER) : roles;
        // Default to enabled if not specified
        enabled = enabled != null ? enabled : true;
    }
}