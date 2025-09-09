package dev.fincke.hopper.order.buyer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

// * Repository
// Data access layer for Buyer entities (Spring Data JPA)
public interface BuyerRepository extends JpaRepository<Buyer, UUID>
{
    // find buyer by email address
    Buyer findByEmail(String email);

    // find buyers by name (case-insensitive)
    List<Buyer> findByNameContainingIgnoreCase(String name);

    // check if email exists
    boolean existsByEmail(String email);
}