package dev.fincke.hopper.catalog.listing;

import dev.fincke.hopper.platform.platform.Platform;
import dev.fincke.hopper.catalog.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ListingRepository extends JpaRepository<Listing, UUID>
{
    List<Listing> findByProduct(Product product);
    List<Listing> findByPlatform(Platform platform);
    List<Listing> findByStatus(String status);

    List<Listing> findByProductId(UUID productId);
    List<Listing> findByPlatformId(UUID platformId);

    Optional<Listing> findByPlatformAndExternalListingId(Platform platform, String externalListingId);
}

