package dev.fincke.hopper.auth;

import dev.fincke.hopper.auth.dto.AuthResponse;
import dev.fincke.hopper.auth.dto.LoginRequest;
import dev.fincke.hopper.auth.dto.RefreshTokenRequest;
import dev.fincke.hopper.auth.dto.TokenValidationResponse;
import dev.fincke.hopper.config.JwtProperties;
import dev.fincke.hopper.security.CustomUserDetailsService;
import dev.fincke.hopper.security.UserPrincipal;
import dev.fincke.hopper.security.jwt.JwtUtils;
import dev.fincke.hopper.user.User;
import dev.fincke.hopper.user.UserService;
import dev.fincke.hopper.user.dto.UserResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

// REST controller for authentication operations (login, refresh, logout)
@RestController
@RequestMapping("/api/auth")
public class AuthController
{
    // * Attributes
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    // Authentication manager for credential validation
    private final AuthenticationManager authenticationManager;
    
    // JWT utilities for token operations
    private final JwtUtils jwtUtils;
    
    // User details service for loading users
    private final CustomUserDetailsService userDetailsService;
    
    // User service for business operations
    private final UserService userService;
    
    // JWT configuration properties
    private final JwtProperties jwtProperties;
    
    // * Constructor
    
    // Constructor with all required dependencies
    public AuthController(AuthenticationManager authenticationManager,
                         JwtUtils jwtUtils,
                         CustomUserDetailsService userDetailsService,
                         UserService userService,
                         JwtProperties jwtProperties)
    {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.jwtProperties = jwtProperties;
    }
    
    // * Authentication Endpoints
    
    // POST /api/auth/login - authenticate user and return JWT tokens
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest,
                                            HttpServletResponse response)
    {
        try
        {
            logger.info("Login attempt for user: {}", loginRequest.usernameOrEmail());
            
            // Authenticate user credentials
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getNormalizedIdentifier(),
                    loginRequest.password()
                )
            );
            
            // Get authenticated user principal
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            
            // Record successful login
            userService.recordSuccessfulLogin(userPrincipal.getId());
            
            // Generate JWT tokens
            String accessToken = loginRequest.rememberMe() 
                ? jwtUtils.generateRememberMeToken(userPrincipal)
                : jwtUtils.generateAccessToken(userPrincipal);
            String refreshToken = jwtUtils.generateRefreshToken(userPrincipal);
            
            // Calculate token expiration times
            long accessExpiresIn = loginRequest.rememberMe()
                ? jwtProperties.getRememberMeExpirationMs() / 1000
                : jwtProperties.getAccessTokenExpirationMs() / 1000;
            long refreshExpiresIn = jwtProperties.getRefreshTokenExpirationMs() / 1000;
            
            // Set secure HTTP-only cookie for refresh token
            setRefreshTokenCookie(response, refreshToken, refreshExpiresIn);
            
            // Create user response
            UserResponse userResponse = UserResponse.from(userPrincipal.getUser());
            
            // Build authentication response
            AuthResponse authResponse = AuthResponse.success(
                accessToken,
                refreshToken,
                jwtProperties.getTokenType(),
                accessExpiresIn,
                refreshExpiresIn,
                userResponse
            );
            
            logger.info("Successful login for user: {} (ID: {})", 
                userPrincipal.getUsername(), userPrincipal.getId());
            
            return ResponseEntity.ok(authResponse);
        }
        catch (BadCredentialsException e)
        {
            logger.warn("Failed login attempt for user: {}", loginRequest.usernameOrEmail());
            
            // Try to record failed login if user exists
            try
            {
                User user = userDetailsService.getUserByUsernameOrEmail(loginRequest.getNormalizedIdentifier());
                userService.recordFailedLogin(user.getId());
            }
            catch (Exception ex)
            {
                // User not found, ignore for security (don't reveal user existence)
                logger.debug("User not found for failed login: {}", loginRequest.usernameOrEmail());
            }
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(null);
        }
        catch (Exception e)
        {
            logger.error("Login error for user: {}", loginRequest.usernameOrEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }
    
    // POST /api/auth/refresh - refresh access token using refresh token
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest refreshRequest,
                                              HttpServletRequest request,
                                              HttpServletResponse response)
    {
        try
        {
            String refreshToken = refreshRequest.getTrimmedRefreshToken();
            
            // If no token in body, try to get from cookie
            if (refreshToken == null || refreshToken.isEmpty())
            {
                refreshToken = getRefreshTokenFromCookie(request);
            }
            
            if (refreshToken == null || !jwtUtils.validateRefreshToken(refreshToken))
            {
                logger.warn("Invalid refresh token provided");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(null);
            }
            
            // Extract user ID from refresh token
            UUID userId = jwtUtils.getUserIdFromToken(refreshToken);
            if (userId == null)
            {
                logger.warn("No user ID found in refresh token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(null);
            }
            
            // Load user details
            UserDetails userDetails = userDetailsService.loadUserById(userId);
            UserPrincipal userPrincipal = (UserPrincipal) userDetails;
            
            // Generate new tokens
            String newAccessToken = jwtUtils.generateAccessToken(userPrincipal);
            String newRefreshToken = jwtUtils.generateRefreshToken(userPrincipal);
            
            // Calculate expiration times
            long accessExpiresIn = jwtProperties.getAccessTokenExpirationMs() / 1000;
            long refreshExpiresIn = jwtProperties.getRefreshTokenExpirationMs() / 1000;
            
            // Update refresh token cookie
            setRefreshTokenCookie(response, newRefreshToken, refreshExpiresIn);
            
            // Build user info from existing principal
            AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                userPrincipal.getId(),
                userPrincipal.getUsername(),
                userPrincipal.getEmail(),
                userPrincipal.getRoles(),
                userPrincipal.isEnabled(),
                !userPrincipal.isAccountNonLocked(),
                LocalDateTime.now()
            );
            
            // Build refresh response
            AuthResponse authResponse = AuthResponse.refreshSuccess(
                newAccessToken,
                newRefreshToken,
                jwtProperties.getTokenType(),
                accessExpiresIn,
                refreshExpiresIn,
                userInfo
            );
            
            logger.debug("Token refreshed for user: {} (ID: {})", 
                userPrincipal.getUsername(), userPrincipal.getId());
            
            return ResponseEntity.ok(authResponse);
        }
        catch (Exception e)
        {
            logger.error("Token refresh error", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(null);
        }
    }
    
    // POST /api/auth/logout - logout user (client-side token clearing)
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response)
    {
        try
        {
            // Clear refresh token cookie
            clearRefreshTokenCookie(response);
            
            // Clear security context
            SecurityContextHolder.clearContext();
            
            logger.debug("User logged out successfully");
            
            return ResponseEntity.ok(Map.of(
                "message", "Logged out successfully",
                "timestamp", LocalDateTime.now().toString()
            ));
        }
        catch (Exception e)
        {
            logger.error("Logout error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Logout failed"));
        }
    }
    
    // GET /api/auth/me - get current authenticated user information
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser()
    {
        try
        {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal))
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            UserResponse userResponse = UserResponse.from(userPrincipal.getUser());
            
            return ResponseEntity.ok(userResponse);
        }
        catch (Exception e)
        {
            logger.error("Error getting current user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    // POST /api/auth/validate - validate JWT token
    @PostMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validateToken(@RequestHeader("Authorization") String authHeader)
    {
        try
        {
            String token = jwtUtils.extractTokenFromHeader(authHeader);
            
            if (token == null)
            {
                return ResponseEntity.ok(TokenValidationResponse.missing());
            }
            
            if (!jwtUtils.validateAccessToken(token))
            {
                return ResponseEntity.ok(TokenValidationResponse.malformed());
            }
            
            // Extract token information
            UUID userId = jwtUtils.getUserIdFromToken(token);
            String username = jwtUtils.getUsernameFromToken(token);
            String email = jwtUtils.getEmailFromToken(token);
            Date expiration = jwtUtils.getExpirationFromToken(token);
            
            if (userId == null || username == null || expiration == null)
            {
                return ResponseEntity.ok(TokenValidationResponse.malformed());
            }
            
            // Check if token is expired
            if (jwtUtils.isTokenExpired(token))
            {
                return ResponseEntity.ok(TokenValidationResponse.expired());
            }
            
            // Load user to verify account status
            try
            {
                UserDetails userDetails = userDetailsService.loadUserById(userId);
                UserPrincipal userPrincipal = (UserPrincipal) userDetails;
                
                if (!userPrincipal.isEnabled())
                {
                    return ResponseEntity.ok(TokenValidationResponse.accountDisabled());
                }
                
                if (!userPrincipal.isAccountNonLocked())
                {
                    return ResponseEntity.ok(TokenValidationResponse.accountLocked());
                }
                
                // Calculate remaining time
                long remainingTime = (expiration.getTime() - System.currentTimeMillis()) / 1000;
                
                return ResponseEntity.ok(TokenValidationResponse.valid(
                    LocalDateTime.ofInstant(expiration.toInstant(), ZoneOffset.UTC),
                    remainingTime,
                    userPrincipal.getId(),
                    userPrincipal.getUsername(),
                    userPrincipal.getEmail(),
                    userPrincipal.getRoles(),
                    userPrincipal.isEnabled(),
                    !userPrincipal.isAccountNonLocked()
                ));
            }
            catch (Exception e)
            {
                return ResponseEntity.ok(TokenValidationResponse.userNotFound());
            }
        }
        catch (Exception e)
        {
            logger.error("Token validation error", e);
            return ResponseEntity.ok(TokenValidationResponse.malformed());
        }
    }
    
    // * Cookie Management Methods
    
    // Set secure HTTP-only cookie for refresh token
    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken, long expiresIn)
    {
        Cookie cookie = new Cookie(jwtProperties.getRefreshTokenCookieName(), refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true in production with HTTPS
        cookie.setPath("/");
        cookie.setMaxAge((int) expiresIn);
        cookie.setAttribute("SameSite", "Strict");
        
        response.addCookie(cookie);
    }
    
    // Get refresh token from HTTP-only cookie
    private String getRefreshTokenFromCookie(HttpServletRequest request)
    {
        Cookie[] cookies = request.getCookies();
        if (cookies == null)
        {
            return null;
        }
        
        for (Cookie cookie : cookies)
        {
            if (jwtProperties.getRefreshTokenCookieName().equals(cookie.getName()))
            {
                return cookie.getValue();
            }
        }
        
        return null;
    }
    
    // Clear refresh token cookie on logout
    private void clearRefreshTokenCookie(HttpServletResponse response)
    {
        Cookie cookie = new Cookie(jwtProperties.getRefreshTokenCookieName(), "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true in production with HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(0); // Expire immediately
        cookie.setAttribute("SameSite", "Strict");
        
        response.addCookie(cookie);
    }
}