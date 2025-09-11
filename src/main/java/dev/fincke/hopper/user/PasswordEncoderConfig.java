package dev.fincke.hopper.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// Configuration for password encoding using BCrypt with high security strength
@Configuration
public class PasswordEncoderConfig
{
    
    // BCrypt strength level (12 rounds for high security with reasonable performance)
    private static final int BCRYPT_STRENGTH = 12;
    
    // Password encoder bean for dependency injection across the application
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        // BCrypt with 12 rounds provides strong security against brute force attacks
        // Each round doubles the computation time, making attacks exponentially harder
        return new BCryptPasswordEncoder(BCRYPT_STRENGTH);
    }
}