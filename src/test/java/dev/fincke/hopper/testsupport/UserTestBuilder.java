package dev.fincke.hopper.testsupport;

import dev.fincke.hopper.user.Role;
import dev.fincke.hopper.user.RoleType;
import dev.fincke.hopper.user.User;

import java.util.Set;
import java.util.UUID;

// Builder for User aggregates to keep security tests readable
// Creates users with secure defaults for authentication and authorization testing
public final class UserTestBuilder
{
    // * Default Test Values
    
    // Auto-generated unique ID for each test user
    private UUID id = UUID.randomUUID();
    // Standard username format for authentication testing
    private String username = "jdoe";
    // Realistic email address for user identification and notifications
    private String email = "jdoe@example.com";
    // Pre-hashed password (BCrypt format) for security testing
    private String password = "$2a$10$0123456789abcdef0123456789abcdef0123456789abcdef012345";
    // Enabled by default (most users are active)
    private boolean enabled = true;
    // Not locked by default (account lockout is exception case)
    private boolean accountLocked = false;
    // Default to USER role (standard permission level)
    private Set<Role> roles = Set.of(new Role(RoleType.USER));

    // * Factory Method
    
    // Private constructor enforces builder pattern usage
    private UserTestBuilder()
    {
    }

    // Factory method to start builder chain
    public static UserTestBuilder user()
    {
        return new UserTestBuilder();
    }

    // * Builder Methods
    
    // Override default ID (useful for relationship and lookup testing)
    public UserTestBuilder withId(UUID id)
    {
        this.id = id;
        return this;
    }

    // Override default username (for authentication and uniqueness testing)
    public UserTestBuilder withUsername(String username)
    {
        this.username = username;
        return this;
    }

    // Override default email (for notification and uniqueness testing)
    public UserTestBuilder withEmail(String email)
    {
        this.email = email;
        return this;
    }

    // Override default password (for authentication testing with specific credentials)
    public UserTestBuilder withPassword(String password)
    {
        this.password = password;
        return this;
    }

    // Set user as disabled (for account state testing)
    public UserTestBuilder asDisabled()
    {
        this.enabled = false;
        return this;
    }

    // Set user as locked (for security and account lockout testing)
    public UserTestBuilder asLocked()
    {
        this.accountLocked = true;
        return this;
    }

    // Override default roles (for authorization and permission testing)
    public UserTestBuilder withRoles(Set<Role> roles)
    {
        this.roles = roles;
        return this;
    }

    // * Entity Construction
    
    // Builds User entity with configured values
    public User build()
    {
        // Create user using main constructor (validates required fields)
        User user = new User(username, email, password);
        // Set ID manually (simulates database ID assignment)
        user.setId(id);
        // Configure account state for security testing
        user.setEnabled(enabled);
        user.setAccountLocked(accountLocked);
        // Assign roles for authorization testing
        user.setRoles(roles);
        return user;
    }
}
