package dev.fincke.hopper.user;

import dev.fincke.hopper.user.dto.PasswordChangeRequest;
import dev.fincke.hopper.user.dto.UserCreateRequest;
import dev.fincke.hopper.user.dto.UserResponse;
import dev.fincke.hopper.user.dto.UserUpdateRequest;

import java.util.List;
import java.util.UUID;

// Service interface defining user management business operations
public interface UserService
{
    
    // * Core CRUD Operations
    
    // Create new user from request data with password encryption
    UserResponse createUser(UserCreateRequest request);
    
    // Update existing user by ID
    UserResponse updateUser(UUID id, UserUpdateRequest request);
    
    // Find user by ID (throws exception if not found)
    UserResponse findById(UUID id);
    
    // Retrieve all users
    List<UserResponse> findAll();
    
    // Delete user by ID
    void deleteUser(UUID id);
    
    // * Authentication Operations
    
    // Find user by username
    UserResponse findByUsername(String username);
    
    // Find user by email address
    UserResponse findByEmail(String email);
    
    // Find user by username or email (flexible login)
    UserResponse findByUsernameOrEmail(String identifier);
    
    // Check if username exists
    boolean existsByUsername(String username);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // * Password Operations
    
    // Change user password with current password verification
    void changePassword(UUID userId, PasswordChangeRequest request);
    
    // Reset password (admin operation, no current password required)
    void resetPassword(UUID userId, String newPassword);
    
    // Verify password against user's stored password
    boolean verifyPassword(UUID userId, String password);
    
    // * Account Management Operations
    
    // Lock user account
    void lockAccount(UUID userId);
    
    // Unlock user account
    void unlockAccount(UUID userId);
    
    // Enable user account
    void enableAccount(UUID userId);
    
    // Disable user account
    void disableAccount(UUID userId);
    
    // Increment failed login attempts and lock if threshold reached
    void recordFailedLogin(UUID userId);
    
    // Reset failed login attempts (successful login)
    void recordSuccessfulLogin(UUID userId);
    
    // * Role Management Operations
    
    // Add role to user
    void addRole(UUID userId, RoleType roleType);
    
    // Remove role from user
    void removeRole(UUID userId, RoleType roleType);
    
    // Find users with specific role
    List<UserResponse> findByRole(RoleType roleType);
    
    // * Search Operations
    
    // Search users by username (case-insensitive partial match)
    List<UserResponse> searchByUsername(String username);
    
    // Search users by email (case-insensitive partial match)
    List<UserResponse> searchByEmail(String email);
    
    // Find enabled users
    List<UserResponse> findEnabledUsers();
    
    // Find locked users
    List<UserResponse> findLockedUsers();
    
    // * Validation Operations
    
    // Validate user data and business rules
    void validateUser(UUID id);
    
    // Check if password meets complexity requirements
    boolean isValidPassword(String password);
    
    // Check if username format is valid
    boolean isValidUsername(String username);
    
    // Check if email format is valid
    boolean isValidEmail(String email);
}