package dev.fincke.hopper.security;

import dev.fincke.hopper.user.User;
import dev.fincke.hopper.user.UserRepository;
import dev.fincke.hopper.testsupport.UserTestBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

// Tests for CustomUserDetailsService covering flexible lookups and guards
// Tests Spring Security integration for user authentication and principal creation
// Enables Mockito framework for dependency injection testing
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class CustomUserDetailsServiceTest
{
    // * Test Dependencies
    
    // Repository mock for user lookup operations
    @Mock
    private UserRepository userRepository;

    // Service under test with dependencies injected by Mockito
    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    // * Username/Email Lookup Tests

    // Tests successful user lookup by username with UserDetails conversion
    @Test
    @DisplayName("loadUserByUsername returns principal when username exists")
    void loadUserByUsername_ReturnsPrincipal()
    {
        // Test user with both username and email for flexible lookup
        User user = UserTestBuilder.user().withUsername("jane").withEmail("jane@example.com").build();

        // Mock successful user lookup by username or email
        when(userRepository.findByUsernameOrEmail("jane")).thenReturn(Optional.of(user));

        // Test Spring Security UserDetails conversion
        UserDetails details = userDetailsService.loadUserByUsername("jane");

        // Verify UserDetails contains correct username and is enabled
        assertEquals("jane", details.getUsername());
        assertTrue(details.isEnabled());  // User should be enabled by default
    }

    // Tests Spring Security exception handling for missing users
    @Test
    @DisplayName("loadUserByUsername throws when user missing")
    void loadUserByUsername_ThrowsWhenMissing()
    {
        // Mock user not found for authentication failure test
        when(userRepository.findByUsernameOrEmail("missing")).thenReturn(Optional.empty());
        // Verify Spring Security standard exception for authentication failures
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("missing"));
    }

    // * ID-Based Lookup Tests

    // Tests user lookup by UUID for JWT token validation
    @Test
    @DisplayName("loadUserById returns principal for existing user")
    void loadUserById_ReturnsPrincipal()
    {
        // Test user for ID-based lookup (used in JWT authentication)
        User user = UserTestBuilder.user().build();
        UUID userId = user.getId();

        // Mock successful user lookup by ID
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Test UserPrincipal creation from User entity
        UserDetails details = userDetailsService.loadUserById(userId);

        // Verify UserPrincipal contains correct user ID
        assertEquals(userId, ((UserPrincipal) details).getId());
    }

    // Tests email normalization for case-insensitive authentication
    @Test
    @DisplayName("loadUserByEmail normalizes case before lookup")
    void loadUserByEmail_LowercasesInput()
    {
        // Test user with lowercase email (stored format)
        User user = UserTestBuilder.user().withEmail("admin@example.com").build();

        // Mock repository expects normalized lowercase email
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

        // Test with mixed case input to verify normalization
        UserDetails details = userDetailsService.loadUserByEmail("Admin@Example.com");

        // Verify email was normalized and user found correctly
        assertEquals("admin@example.com", ((UserPrincipal) details).getEmail());
    }

    // * Existence Check Tests

    // Tests user existence check for registration validation
    @Test
    @DisplayName("userExists returns false when lookup fails")
    void userExists_ReturnsFalseWhenMissing()
    {
        // Mock user not found for existence check
        when(userRepository.findByUsernameOrEmail("ghost"))
            .thenReturn(Optional.empty());

        // Verify existence check returns false for missing users
        assertFalse(userDetailsService.userExists("ghost"));
    }

    // Tests ID-based existence check for authorization operations
    @Test
    @DisplayName("userExistsById returns true when user is present")
    void userExistsById_ReturnsTrue()
    {
        // Test user for ID-based existence check
        User user = UserTestBuilder.user().build();
        // Mock user found by ID
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Verify existence check returns true for existing user
        assertTrue(userDetailsService.userExistsById(user.getId()));
    }
}
