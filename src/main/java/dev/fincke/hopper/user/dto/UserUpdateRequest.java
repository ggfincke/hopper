package dev.fincke.hopper.user.dto;

import dev.fincke.hopper.user.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.Set;

// Request DTO for updating existing users (all fields optional for partial updates)
public record UserUpdateRequest(
    
    // Username (optional, 3-50 characters if provided)
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    String username,
    
    // Email address (optional, validated format if provided)
    @Email(message = "Email must be a valid email address")
    String email,
    
    // User roles (optional, replaces existing roles if provided)
    Set<RoleType> roles,
    
    // Account enabled status (optional)
    Boolean enabled,
    
    // Account locked status (optional)
    Boolean accountLocked
    
)
{
    
    // Compact constructor normalizes data
    public UserUpdateRequest
    {
        // Normalize username by trimming whitespace
        username = username != null ? username.trim() : username;
        // Normalize email to lowercase for consistency
        email = email != null ? email.trim().toLowerCase() : email;
    }
    
    // Check if request contains any updates (at least one field provided)
    public boolean hasUpdates()
    {
        return username != null || email != null || roles != null || 
               enabled != null || accountLocked != null;
    }
}