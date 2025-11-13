package dev.fincke.hopper.config;

import dev.fincke.hopper.security.CustomUserDetailsService;
import dev.fincke.hopper.security.jwt.JwtAuthenticationEntryPoint;
import dev.fincke.hopper.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// Spring Security configuration for JWT-based authentication
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig
{
    // * Attributes
    
    // Custom user details service for loading users from database
    private final CustomUserDetailsService userDetailsService;
    
    // JWT authentication entry point for unauthorized access
    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    
    // JWT authentication filter for token validation
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    // Password encoder for authentication
    private final PasswordEncoder passwordEncoder;
    
    // * Constructor
    
    // Constructor with all required dependencies
    public SecurityConfig(CustomUserDetailsService userDetailsService,
                         JwtAuthenticationEntryPoint unauthorizedHandler,
                         JwtAuthenticationFilter jwtAuthenticationFilter,
                         PasswordEncoder passwordEncoder)
    {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.passwordEncoder = passwordEncoder;
    }
    
    // * Bean Definitions
    
    // Authentication manager bean for credential validation
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception
    {
        return authConfig.getAuthenticationManager();
    }
    
    // DAO authentication provider with custom user details service
    @Bean
    public DaoAuthenticationProvider authenticationProvider()
    {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
    
    // CORS configuration source for cross-origin requests
    @Bean
    public CorsConfigurationSource corsConfigurationSource()
    {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow specific origins (configure for your frontend domains)
        configuration.setAllowedOriginPatterns(List.of(
            "http://localhost:*",      // Local development
            "http://127.0.0.1:*",      // Local development alternative
            "https://yourdomain.com"   // Production domain (replace with actual)
        ));
        
        // Allow specific HTTP methods
        configuration.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        
        // Allow specific headers (including Authorization for JWT)
        configuration.setAllowedHeaders(List.of(
            "Authorization", 
            "Content-Type", 
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // Expose headers that client can access
        configuration.setExposedHeaders(List.of(
            "Authorization",
            "Content-Disposition"
        ));
        
        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Cache preflight requests for 1 hour
        configuration.setMaxAge(3600L);
        
        // Apply CORS configuration to all paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
    
    // Security filter chain configuration
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
    {
        http
            // Disable CSRF for stateless JWT authentication
            .csrf(AbstractHttpConfigurer::disable)
            
            // Configure CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Configure exception handling
            .exceptionHandling(exception -> 
                exception.authenticationEntryPoint(unauthorizedHandler)
            )
            
            // Configure session management (stateless for JWT)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Configure authorization rules
            .authorizeHttpRequests(authz -> authz
                // Public endpoints (no authentication required)
                .requestMatchers(
                    "/api/auth/login",
                    "/api/auth/refresh",
                    "/api/auth/validate",
                    "/error",
                    "/favicon.ico"
                ).permitAll()
                
                // Development-only endpoints
                .requestMatchers(
                    "/h2-console/**",
                    "/actuator/health",
                    "/actuator/info"
                ).permitAll()
                
                // Admin-only endpoints
                .requestMatchers(
                    "/actuator/**",
                    "/api/admin/**"
                ).hasRole("ADMIN")
                
                // API client endpoints
                .requestMatchers(
                    "/api/integrations/**"
                ).hasAnyRole("ADMIN", "API_CLIENT")
                
                // All other API endpoints require authentication
                .requestMatchers("/api/**").authenticated()
                
                // All other requests require authentication
                .anyRequest().authenticated()
            );
        
        // Add custom JWT authentication filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        // Configure authentication provider
        http.authenticationProvider(authenticationProvider());
        
        // Allow H2 console in development (disable frame options)
        http.headers(headers -> headers
            .frameOptions(frame -> frame.sameOrigin())  // Allow H2 console frames
        );
        
        return http.build();
    }
}
