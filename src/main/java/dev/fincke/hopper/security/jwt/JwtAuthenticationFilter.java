package dev.fincke.hopper.security.jwt;

import dev.fincke.hopper.config.JwtProperties;
import dev.fincke.hopper.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

// JWT authentication filter that validates tokens and sets security context
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
    // * Attributes
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    // JWT utilities for token operations
    private final JwtUtils jwtUtils;
    
    // User details service for loading users
    private final CustomUserDetailsService userDetailsService;
    
    // JWT configuration properties
    private final JwtProperties jwtProperties;
    
    // * Constructor
    
    // Constructor with required dependencies
    public JwtAuthenticationFilter(JwtUtils jwtUtils, 
                                 CustomUserDetailsService userDetailsService,
                                 JwtProperties jwtProperties)
    {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
        this.jwtProperties = jwtProperties;
    }
    
    // * Filter Implementation
    
    // Process JWT authentication for each request
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                  @NonNull HttpServletResponse response,
                                  @NonNull FilterChain filterChain) throws ServletException, IOException
    {
        try
        {
            // Extract JWT token from request (header or cookie)
            String jwt = parseJwtFromRequest(request);
            
            if (jwt != null && jwtUtils.validateAccessToken(jwt))
            {
                // Extract user information from valid token
                UUID userId = jwtUtils.getUserIdFromToken(jwt);
                
                if (userId != null)
                {
                    // Load user details from database
                    UserDetails userDetails = userDetailsService.loadUserById(userId);
                    
                    // Create authentication token with user details
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails, 
                            null, 
                            userDetails.getAuthorities()
                        );
                    
                    // Set authentication details from web request
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    logger.debug("Set authentication for user: {} (ID: {})", 
                        userDetails.getUsername(), userId);
                }
                else
                {
                    logger.warn("User ID not found in JWT token");
                }
            }
            else if (jwt != null)
            {
                logger.debug("Invalid or expired JWT token");
            }
        }
        catch (Exception ex)
        {
            logger.error("Could not set user authentication in security context", ex);
        }
        
        // Continue with filter chain
        filterChain.doFilter(request, response);
    }
    
    // * Token Extraction Methods
    
    // Extract JWT token from request headers or cookies
    private String parseJwtFromRequest(HttpServletRequest request)
    {
        // First try to get token from Authorization header
        String headerAuth = request.getHeader(jwtProperties.getHeaderName());
        String token = jwtUtils.extractTokenFromHeader(headerAuth);
        
        if (token != null)
        {
            logger.debug("JWT token found in Authorization header");
            return token;
        }
        
        // Fallback to cookie-based token (for refresh tokens or secure storage)
        token = parseJwtFromCookie(request);
        if (token != null)
        {
            logger.debug("JWT token found in cookie");
            return token;
        }
        
        // No token found
        return null;
    }
    
    // Extract JWT token from cookies
    private String parseJwtFromCookie(HttpServletRequest request)
    {
        Cookie[] cookies = request.getCookies();
        if (cookies == null)
        {
            return null;
        }
        
        // Look for token in access-token cookie first
        for (Cookie cookie : cookies)
        {
            if ("access-token".equals(cookie.getName()))
            {
                String value = cookie.getValue();
                if (StringUtils.hasText(value))
                {
                    return value;
                }
            }
        }
        
        // Look for token in refresh-token cookie as fallback
        for (Cookie cookie : cookies)
        {
            if (jwtProperties.getRefreshTokenCookieName().equals(cookie.getName()))
            {
                String value = cookie.getValue();
                if (StringUtils.hasText(value) && jwtUtils.validateAccessToken(value))
                {
                    return value;
                }
            }
        }
        
        return null;
    }
    
    // * Filter Configuration
    
    // Skip JWT authentication for public endpoints
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request)
    {
        String path = request.getRequestURI();
        
        // Skip authentication for public endpoints
        return path.startsWith("/api/auth/login") ||
               path.startsWith("/api/auth/register") ||
               path.startsWith("/h2-console") ||
               path.startsWith("/actuator") ||
               path.startsWith("/error") ||
               path.equals("/favicon.ico");
    }
}