package dev.fincke.hopper.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Data access layer for User entities (Spring Data JPA)
public interface UserRepository extends JpaRepository<User, UUID>
{
    // Find user by username (case-sensitive for security)
    Optional<User> findByUsername(String username);

    // Find user by email address (normalized to lowercase)
    Optional<User> findByEmail(String email);

    // Find user by username or email (for flexible login)
    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);

    // Check if username exists (case-sensitive)
    boolean existsByUsername(String username);

    // Check if email exists (case-insensitive due to normalization)
    boolean existsByEmail(String email);

    // Find users by enabled status
    List<User> findByEnabled(boolean enabled);

    // Find users by account locked status
    List<User> findByAccountLocked(boolean accountLocked);

    // Find users with specific role
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleType")
    List<User> findByRole(@Param("roleType") RoleType roleType);

    // Find users by username containing (case-insensitive search)
    List<User> findByUsernameContainingIgnoreCase(String username);

    // Find users by email containing (case-insensitive search)
    List<User> findByEmailContainingIgnoreCase(String email);

    // Count users with failed login attempts greater than threshold
    @Query("SELECT COUNT(u) FROM User u WHERE u.failedLoginAttempts >= :threshold")
    long countUsersWithFailedAttempts(@Param("threshold") int threshold);

    // Find users with multiple failed login attempts
    List<User> findByFailedLoginAttemptsGreaterThanEqual(int threshold);
}