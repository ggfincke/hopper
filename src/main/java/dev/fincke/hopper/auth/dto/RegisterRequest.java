package dev.fincke.hopper.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Request DTO for user self-service registration
public record RegisterRequest(
    
    // Desired username for the new account
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    String username,
    
    // Email address for communications/login
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    String email,
    
    // Account password
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    String password,
    
    // Optional remember me flag to control token lifetime
    boolean rememberMe
) {
}
