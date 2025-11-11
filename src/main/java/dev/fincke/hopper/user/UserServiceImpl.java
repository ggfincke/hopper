package dev.fincke.hopper.user;

import dev.fincke.hopper.user.dto.PasswordChangeRequest;
import dev.fincke.hopper.user.dto.UserCreateRequest;
import dev.fincke.hopper.user.dto.UserResponse;
import dev.fincke.hopper.user.dto.UserUpdateRequest;
import dev.fincke.hopper.user.exception.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// Service implementation for user management business operations with transaction management
// Spring service with read-only transactions by default
@Service
@Transactional(readOnly = true)
@SuppressWarnings("null")
public class UserServiceImpl implements UserService
{
    
    // * Dependencies
    
    // Repository for user data access
    private final UserRepository userRepository;
    
    // Repository for role data access
    private final RoleRepository roleRepository;
    
    // BCrypt password encoder for secure password hashing
    private final PasswordEncoder passwordEncoder;
    
    // * Validation Patterns
    
    // Username pattern: alphanumeric, underscore, hyphen (3-50 chars)
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{3,50}$");
    
    // Email pattern for additional validation beyond @Email annotation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    // Password complexity: at least 8 chars, one uppercase, one lowercase, one digit
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$"
    );
    
    // Maximum failed login attempts before account lock
    private static final int MAX_FAILED_ATTEMPTS = 5;
    
    // * Constructor
    
    // Constructor injection for all dependencies
    public UserServiceImpl(UserRepository userRepository, 
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    // * Core CRUD Operations
    
    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request)
    {
        
        // Validate username uniqueness
        if (userRepository.existsByUsername(request.username()))
        {
            throw new DuplicateUsernameException(request.username());
        }
        
        // Validate email uniqueness
        if (userRepository.existsByEmail(request.email()))
        {
            throw new DuplicateUserEmailException(request.email());
        }
        
        // Validate password complexity
        if (!isValidPassword(request.password()))
        {
            throw new InvalidPasswordException("Password must contain at least 8 characters, one uppercase, one lowercase, and one digit");
        }
        
        // Create user entity with encrypted password
        User user = new User(request.username(), request.email(), 
                            passwordEncoder.encode(request.password()));
        user.setEnabled(request.enabled());
        
        // Assign roles to user
        Set<Role> roles = new HashSet<>();
        for (RoleType roleType : request.roles())
        {
            Role role = roleRepository.findByName(roleType)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleType));
            roles.add(role);
        }
        user.setRoles(roles);
        
        // Save and return response DTO
        User savedUser = userRepository.save(user);
        return UserResponse.from(savedUser);
    }
    
    @Override
    @Transactional
    public UserResponse updateUser(UUID id, UserUpdateRequest request)
    {
        
        // Find existing user
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        
        // Validate that at least one field is provided for update
        if (!request.hasUpdates())
        {
            throw new IllegalArgumentException("At least one field must be provided for update");
        }
        
        // Update username if provided and different
        if (request.username() != null && !request.username().equals(user.getUsername()))
        {
            if (userRepository.existsByUsername(request.username()))
            {
                throw new DuplicateUsernameException(request.username());
            }
            user.setUsername(request.username());
        }
        
        // Update email if provided and different
        if (request.email() != null && !request.email().equals(user.getEmail()))
        {
            if (userRepository.existsByEmail(request.email()))
            {
                throw new DuplicateUserEmailException(request.email());
            }
            user.setEmail(request.email());
        }
        
        // Update roles if provided
        if (request.roles() != null)
        {
            Set<Role> roles = new HashSet<>();
            for (RoleType roleType : request.roles())
            {
                Role role = roleRepository.findByName(roleType)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleType));
                roles.add(role);
            }
            user.setRoles(roles);
        }
        
        // Update account status if provided
        if (request.enabled() != null)
        {
            user.setEnabled(request.enabled());
        }
        
        if (request.accountLocked() != null)
        {
            user.setAccountLocked(request.accountLocked());
            if (!request.accountLocked())
            {
                user.resetFailedLoginAttempts();
            }
        }
        
        User savedUser = userRepository.save(user);
        return UserResponse.from(savedUser);
    }
    
    @Override
    public UserResponse findById(UUID id)
    {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        return UserResponse.from(user);
    }
    
    @Override
    public List<UserResponse> findAll()
    {
        return userRepository.findAll().stream()
            .map(UserResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void deleteUser(UUID id)
    {
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));

        if (user.isEnabled())
        {
            throw new UserDeletionNotAllowedException(id, "disable the account before deletion");
        }

        userRepository.deleteById(id);
    }
    
    // * Authentication Operations
    
    @Override
    public UserResponse findByUsername(String username)
    {
        
        if (username == null || username.trim().isEmpty())
        {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        
        String normalizedUsername = username.trim();
        User user = userRepository.findByUsername(normalizedUsername)
            .orElseThrow(() -> new UserNotFoundException(normalizedUsername, true));
        
        return UserResponse.from(user);
    }
    
    @Override
    public UserResponse findByEmail(String email)
    {
        
        if (email == null || email.trim().isEmpty())
        {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        String normalizedEmail = email.trim().toLowerCase();
        User user = userRepository.findByEmail(normalizedEmail)
            .orElseThrow(() -> new UserNotFoundException(normalizedEmail, false));
        
        return UserResponse.from(user);
    }
    
    @Override
    public UserResponse findByUsernameOrEmail(String identifier)
    {
        
        if (identifier == null || identifier.trim().isEmpty())
        {
            throw new IllegalArgumentException("Identifier cannot be null or empty");
        }
        
        String normalizedIdentifier = identifier.trim().toLowerCase();
        User user = userRepository.findByUsernameOrEmail(normalizedIdentifier)
            .orElseThrow(() -> new UserNotFoundException(normalizedIdentifier));
        
        return UserResponse.from(user);
    }
    
    @Override
    public boolean existsByUsername(String username)
    {
        
        if (username == null || username.trim().isEmpty())
        {
            return false;
        }
        
        return userRepository.existsByUsername(username.trim());
    }
    
    @Override
    public boolean existsByEmail(String email)
    {
        
        if (email == null || email.trim().isEmpty())
        {
            return false;
        }
        
        return userRepository.existsByEmail(email.trim().toLowerCase());
    }
    
    // * Password Operations
    
    @Override
    @Transactional
    public void changePassword(UUID userId, PasswordChangeRequest request)
    {
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        // Verify current password
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword()))
        {
            throw new InvalidPasswordException("Current password is incorrect");
        }
        
        // Validate new password complexity
        if (!isValidPassword(request.newPassword()))
        {
            throw new InvalidPasswordException("New password must contain at least 8 characters, one uppercase, one lowercase, and one digit");
        }
        
        // Encrypt and set new password
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void resetPassword(UUID userId, String newPassword)
    {
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        // Validate new password complexity
        if (!isValidPassword(newPassword))
        {
            throw new InvalidPasswordException("New password must contain at least 8 characters, one uppercase, one lowercase, and one digit");
        }
        
        // Encrypt and set new password (admin operation, no current password verification)
        user.setPassword(passwordEncoder.encode(newPassword));
        user.resetFailedLoginAttempts(); // Reset failed attempts on password reset
        userRepository.save(user);
    }
    
    @Override
    public boolean verifyPassword(UUID userId, String password)
    {
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        return passwordEncoder.matches(password, user.getPassword());
    }
    
    // * Account Management Operations
    
    @Override
    @Transactional
    public void lockAccount(UUID userId)
    {
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        user.lockAccount();
        userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void unlockAccount(UUID userId)
    {
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        user.unlockAccount();
        userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void enableAccount(UUID userId)
    {
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        user.setEnabled(true);
        userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void disableAccount(UUID userId)
    {
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        user.setEnabled(false);
        userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void recordFailedLogin(UUID userId)
    {
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        user.incrementFailedLoginAttempts();
        
        // Lock account if threshold reached
        if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS)
        {
            user.lockAccount();
        }
        
        userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void recordSuccessfulLogin(UUID userId)
    {
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        user.resetFailedLoginAttempts();
        userRepository.save(user);
    }
    
    // * Role Management Operations
    
    @Override
    @Transactional
    public void addRole(UUID userId, RoleType roleType)
    {
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        Role role = roleRepository.findByName(roleType)
            .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleType));
        
        user.addRole(role);
        userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void removeRole(UUID userId, RoleType roleType)
    {
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        Role role = roleRepository.findByName(roleType)
            .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleType));
        
        user.removeRole(role);
        userRepository.save(user);
    }
    
    @Override
    public List<UserResponse> findByRole(RoleType roleType)
    {
        return userRepository.findByRole(roleType).stream()
            .map(UserResponse::from)
            .collect(Collectors.toList());
    }
    
    // * Search Operations
    
    @Override
    public List<UserResponse> searchByUsername(String username)
    {
        
        if (username == null || username.trim().isEmpty())
        {
            throw new IllegalArgumentException("Search username cannot be null or empty");
        }
        
        String searchTerm = username.trim();
        return userRepository.findByUsernameContainingIgnoreCase(searchTerm).stream()
            .map(UserResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<UserResponse> searchByEmail(String email)
    {
        
        if (email == null || email.trim().isEmpty())
        {
            throw new IllegalArgumentException("Search email cannot be null or empty");
        }
        
        String searchTerm = email.trim();
        return userRepository.findByEmailContainingIgnoreCase(searchTerm).stream()
            .map(UserResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<UserResponse> findEnabledUsers()
    {
        return userRepository.findByEnabled(true).stream()
            .map(UserResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<UserResponse> findLockedUsers()
    {
        return userRepository.findByAccountLocked(true).stream()
            .map(UserResponse::from)
            .collect(Collectors.toList());
    }
    
    // * Validation Operations
    
    @Override
    public void validateUser(UUID id)
    {
        
        if (!userRepository.existsById(id))
        {
            throw new UserNotFoundException(id);
        }
        
        // Additional validation logic can be added here
        // such as checking account status, role assignments, etc.
    }
    
    @Override
    public boolean isValidPassword(String password)
    {
        
        if (password == null || password.trim().isEmpty())
        {
            return false;
        }
        
        // Check password complexity requirements
        return PASSWORD_PATTERN.matcher(password).matches();
    }
    
    @Override
    public boolean isValidUsername(String username)
    {
        
        if (username == null || username.trim().isEmpty())
        {
            return false;
        }
        
        String trimmedUsername = username.trim();
        
        // Basic length and pattern check
        return USERNAME_PATTERN.matcher(trimmedUsername).matches();
    }
    
    @Override
    public boolean isValidEmail(String email)
    {
        
        if (email == null || email.trim().isEmpty())
        {
            return false;
        }
        
        String trimmedEmail = email.trim();
        
        // Basic length check
        if (trimmedEmail.length() > 254)
        {
            return false;
        }
        
        // Pattern matching for email format
        return EMAIL_PATTERN.matcher(trimmedEmail).matches();
    }
}
