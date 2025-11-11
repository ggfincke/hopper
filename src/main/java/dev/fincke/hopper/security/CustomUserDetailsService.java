package dev.fincke.hopper.security;

import dev.fincke.hopper.user.User;
import dev.fincke.hopper.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

// Custom UserDetailsService implementation for loading users from database
@Service
@SuppressWarnings("null")
public class CustomUserDetailsService implements UserDetailsService
{
    // * Attributes
    
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    
    // User repository for database access
    private final UserRepository userRepository;
    
    // * Constructor
    
    // Constructor with UserRepository dependency injection
    public CustomUserDetailsService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }
    
    // * UserDetailsService Implementation
    
    // Load user by username for Spring Security authentication
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException
    {
        logger.debug("Loading user by username or email: {}", usernameOrEmail);
        
        // Try to find user by username or email (flexible login)
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail)
            .orElseThrow(() -> {
                logger.warn("User not found with username or email: {}", usernameOrEmail);
                return new UsernameNotFoundException("User not found: " + usernameOrEmail);
            });
        
        logger.debug("Successfully loaded user: {} (ID: {})", user.getUsername(), user.getId());
        
        // Wrap User entity in UserPrincipal for Spring Security
        return UserPrincipal.from(user);
    }
    
    // * Additional Loading Methods
    
    // Load user by ID (useful for JWT token validation)
    @Transactional(readOnly = true)
    public UserDetails loadUserById(UUID userId) throws UsernameNotFoundException
    {
        logger.debug("Loading user by ID: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                logger.warn("User not found with ID: {}", userId);
                return new UsernameNotFoundException("User not found with ID: " + userId);
            });
        
        logger.debug("Successfully loaded user by ID: {} ({})", user.getUsername(), userId);
        
        return UserPrincipal.from(user);
    }
    
    // Load user by username only (strict username match)
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsernameStrict(String username) throws UsernameNotFoundException
    {
        logger.debug("Loading user by username (strict): {}", username);
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> {
                logger.warn("User not found with username: {}", username);
                return new UsernameNotFoundException("User not found: " + username);
            });
        
        logger.debug("Successfully loaded user by username: {}", username);
        
        return UserPrincipal.from(user);
    }
    
    // Load user by email only (strict email match)
    @Transactional(readOnly = true)
    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException
    {
        logger.debug("Loading user by email: {}", email);
        
        User user = userRepository.findByEmail(email.toLowerCase())
            .orElseThrow(() -> {
                logger.warn("User not found with email: {}", email);
                return new UsernameNotFoundException("User not found: " + email);
            });
        
        logger.debug("Successfully loaded user by email: {}", email);
        
        return UserPrincipal.from(user);
    }
    
    // * Utility Methods
    
    // Check if user exists by username or email
    public boolean userExists(String usernameOrEmail)
    {
        try
        {
            loadUserByUsername(usernameOrEmail);
            return true;
        }
        catch (UsernameNotFoundException e)
        {
            return false;
        }
    }
    
    // Check if user exists by ID
    public boolean userExistsById(UUID userId)
    {
        try
        {
            loadUserById(userId);
            return true;
        }
        catch (UsernameNotFoundException e)
        {
            return false;
        }
    }
    
    // Get user entity by username or email (for service layer use)
    @Transactional(readOnly = true)
    public User getUserByUsernameOrEmail(String usernameOrEmail) throws UsernameNotFoundException
    {
        UserDetails userDetails = loadUserByUsername(usernameOrEmail);
        return ((UserPrincipal) userDetails).getUser();
    }
    
    // Get user entity by ID (for service layer use)
    @Transactional(readOnly = true)
    public User getUserById(UUID userId) throws UsernameNotFoundException
    {
        UserDetails userDetails = loadUserById(userId);
        return ((UserPrincipal) userDetails).getUser();
    }
}
