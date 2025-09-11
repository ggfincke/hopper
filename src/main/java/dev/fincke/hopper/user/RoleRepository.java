package dev.fincke.hopper.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

// Data access layer for Role entities (Spring Data JPA)
public interface RoleRepository extends JpaRepository<Role, UUID>
{
    // Find role by name enum
    Optional<Role> findByName(RoleType name);

    // Check if role exists by name
    boolean existsByName(RoleType name);
}