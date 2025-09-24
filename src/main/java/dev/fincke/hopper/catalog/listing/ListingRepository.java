package dev.fincke.hopper.catalog.listing;

import dev.fincke.hopper.platform.platform.Platform;
import dev.fincke.hopper.catalog.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// * Repository
// Data-access layer for Listing entities with query methods for service layer
public interface ListingRepository extends JpaRepository<Listing, UUID>
{
    // find listings by product entity
    List<Listing> findByProduct(Product product);
    
    // find listings by platform entity
    List<Listing> findByPlatform(Platform platform);
    
    // find listings by status string
    List<Listing> findByStatus(String status);

    // find listings by product ID
    List<Listing> findByProductId(UUID productId);
    
    // find listings by product ID with pagination support
    Page<Listing> findByProductId(UUID productId, Pageable pageable);
    
    // find listings by platform ID
    List<Listing> findByPlatformId(UUID platformId);
    
    // find listings by platform ID with pagination support
    Page<Listing> findByPlatformId(UUID platformId, Pageable pageable);

    // find listing by platform and external listing ID (unique combination)
    Optional<Listing> findByPlatformAndExternalListingId(Platform platform, String externalListingId);

    // check if any listings exist for given product ID
    boolean existsByProductId(UUID productId);

    // check if any listings exist for given platform ID
    boolean existsByPlatformId(UUID platformId);

    // find listings by status with pagination support
    Page<Listing> findByStatus(String status, Pageable pageable);
}

