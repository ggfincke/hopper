package dev.fincke.hopper.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Request DTO for password changes with current password verification
public record PasswordChangeRequest(
    
    // Current password (required for verification)
    @NotBlank(message = "Current password is required")
    String currentPassword,
    
    // New password (required, minimum 8 characters for security)
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "New password must be at least 8 characters")
    String newPassword
    
)
{
    // No normalization needed for passwords (preserve exactly as entered)
}