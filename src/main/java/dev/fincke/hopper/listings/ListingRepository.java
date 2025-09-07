package dev.fincke.hopper.listings;

import dev.fincke.hopper.platforms.Platform;
import dev.fincke.hopper.products.Product;
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

