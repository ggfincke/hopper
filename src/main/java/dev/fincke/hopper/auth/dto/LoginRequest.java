package dev.fincke.hopper.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Request DTO for user login (username/password authentication)
public record LoginRequest(
    
    // Username or email address for authentication
    @NotBlank(message = "Username or email is required")
    @Size(min = 3, max = 100, message = "Username or email must be between 3 and 100 characters")
    String usernameOrEmail,
    
    // Password for authentication
    @NotBlank(message = "Password is required")
    @Size(min = 1, max = 255, message = "Password cannot be empty")
    String password,
    
    // Optional remember me flag for extended token expiration
    boolean rememberMe
    
)
{
    
    // Constructor with default rememberMe value
    public LoginRequest(String usernameOrEmail, String password)
    {
        this(usernameOrEmail, password, false);
    }
    
    // Get normalized identifier (trimmed and lowercase if email-like)
    public String getNormalizedIdentifier()
    {
        if (usernameOrEmail == null)
        {
            return null;
        }
        
        String trimmed = usernameOrEmail.trim();
        
        // If it looks like an email, normalize to lowercase
        if (trimmed.contains("@"))
        {
            return trimmed.toLowerCase();
        }
        
        // Username remains case-sensitive
        return trimmed;
    }
    
    // Check if identifier appears to be an email address
    public boolean isEmailIdentifier()
    {
        return usernameOrEmail != null && usernameOrEmail.contains("@");
    }
    
    // Check if identifier appears to be a username
    public boolean isUsernameIdentifier()
    {
        return !isEmailIdentifier();
    }
}