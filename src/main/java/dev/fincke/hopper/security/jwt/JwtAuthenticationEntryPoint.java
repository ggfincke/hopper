package dev.fincke.hopper.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

// Entry point for handling unauthorized access attempts (401 responses)
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint
{
    // * Attributes
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);
    
    // JSON object mapper for response serialization
    private final ObjectMapper objectMapper;
    
    // * Constructor
    
    // Constructor with ObjectMapper dependency
    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper)
    {
        this.objectMapper = objectMapper;
    }
    
    // * AuthenticationEntryPoint Implementation
    
    // Handle unauthorized access by returning JSON error response
    @Override
    public void commence(HttpServletRequest request, 
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException
    {
        logger.warn("Unauthorized access attempt to: {} from IP: {}", 
            request.getRequestURI(), getClientIpAddress(request));
        
        // Set response status and content type
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        // Build error response body
        Map<String, Object> errorResponse = buildErrorResponse(request, authException);
        
        // Write JSON response
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
        
        logger.debug("Sent 401 Unauthorized response for: {}", request.getRequestURI());
    }
    
    // * Helper Methods
    
    // Build standardized error response map
    private Map<String, Object> buildErrorResponse(HttpServletRequest request, 
                                                 AuthenticationException authException)
    {
        Map<String, Object> errorResponse = new HashMap<>();
        
        // Standard error fields
        errorResponse.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        errorResponse.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message", "Authentication required to access this resource");
        errorResponse.put("path", request.getRequestURI());
        
        // Additional context for debugging (in development)
        if (isDevelopmentMode())
        {
            errorResponse.put("details", authException.getMessage());
            errorResponse.put("method", request.getMethod());
            errorResponse.put("userAgent", request.getHeader("User-Agent"));
        }
        
        // Authentication hints for client applications
        Map<String, String> authHints = new HashMap<>();
        authHints.put("headerName", "Authorization");
        authHints.put("tokenType", "Bearer");
        authHints.put("loginEndpoint", "/api/auth/login");
        authHints.put("refreshEndpoint", "/api/auth/refresh");
        errorResponse.put("authenticationHints", authHints);
        
        return errorResponse;
    }
    
    // Extract client IP address from request (considering proxies)
    private String getClientIpAddress(HttpServletRequest request)
    {
        // Check for IP in proxy headers first
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty())
        {
            // X-Forwarded-For can contain multiple IPs, take the first one
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty())
        {
            return xRealIp;
        }
        
        // Fallback to remote address
        return request.getRemoteAddr();
    }
    
    // Check if application is running in development mode
    private boolean isDevelopmentMode()
    {
        // Simple check for development mode based on system property or profile
        String activeProfile = System.getProperty("spring.profiles.active", "");
        return activeProfile.contains("dev") || activeProfile.contains("development");
    }
}