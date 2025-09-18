package dev.fincke.hopper.security;

import dev.fincke.hopper.user.Role;
import dev.fincke.hopper.user.RoleType;
import dev.fincke.hopper.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

// Spring Security UserDetails implementation wrapping our User entity
public class UserPrincipal implements UserDetails
{
    // * Attributes
    
    // User entity wrapped by this principal
    private final User user;
    
    // * Constructor
    
    // Constructor that wraps a User entity for Spring Security
    public UserPrincipal(User user)
    {
        if (user == null)
        {
            throw new IllegalArgumentException("User cannot be null");
        }
        this.user = user;
    }
    
    // * UserDetails Implementation
    
    // Get user authorities based on roles (Spring Security requirement)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        // Convert roles to Spring Security authorities with ROLE_ prefix
        return user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
            .collect(Collectors.toSet());
    }
    
    // Get user password for authentication (Spring Security requirement)
    @Override
    public String getPassword()
    {
        return user.getPassword();
    }
    
    // Get username for authentication (Spring Security requirement)
    @Override
    public String getUsername()
    {
        return user.getUsername();
    }
    
    // Check if account is not expired (Spring Security requirement)
    @Override
    public boolean isAccountNonExpired()
    {
        // Account expiration not implemented yet, always return true
        return true;
    }
    
    // Check if account is not locked (Spring Security requirement)
    @Override
    public boolean isAccountNonLocked()
    {
        return !user.isAccountLocked();
    }
    
    // Check if credentials are not expired (Spring Security requirement)
    @Override
    public boolean isCredentialsNonExpired()
    {
        // Credential expiration not implemented yet, always return true
        return true;
    }
    
    // Check if account is enabled (Spring Security requirement)
    @Override
    public boolean isEnabled()
    {
        return user.isEnabled();
    }
    
    // * Additional User Information Methods
    
    // Get user ID
    public UUID getId()
    {
        return user.getId();
    }
    
    // Get user email
    public String getEmail()
    {
        return user.getEmail();
    }
    
    // Get user roles as RoleType enum set
    public Set<RoleType> getRoles()
    {
        return user.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toSet());
    }
    
    // Get failed login attempts count
    public int getFailedLoginAttempts()
    {
        return user.getFailedLoginAttempts();
    }
    
    // Check if user has specific role
    public boolean hasRole(RoleType roleType)
    {
        return user.hasRole(roleType);
    }
    
    // Check if user has admin privileges
    public boolean isAdmin()
    {
        return user.isAdmin();
    }
    
    // Check if user has authority with given name
    public boolean hasAuthority(String authority)
    {
        return getAuthorities().stream()
            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority));
    }
    
    // * Access to Wrapped Entity
    
    // Get the wrapped User entity (use cautiously to avoid tight coupling)
    public User getUser()
    {
        return user;
    }
    
    // * Factory Methods
    
    // Create UserPrincipal from User entity
    public static UserPrincipal from(User user)
    {
        return new UserPrincipal(user);
    }
    
    // * Object Overrides
    
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof UserPrincipal other)) return false;
        // Two principals are equal if they wrap the same user (by ID)
        return user.getId() != null && user.getId().equals(other.user.getId());
    }
    
    @Override
    public int hashCode()
    {
        // Use user ID for hash code consistency with User entity
        return user.getId() != null ? user.getId().hashCode() : 0;
    }
    
    @Override
    public String toString()
    {
        return "UserPrincipal{" +
                "username='" + user.getUsername() + '\'' +
                ", id=" + user.getId() +
                ", enabled=" + user.isEnabled() +
                ", accountLocked=" + user.isAccountLocked() +
                ", roles=" + getRoles() +
                '}';
    }
}