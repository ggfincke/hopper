package dev.fincke.hopper.catalog.product;

import dev.fincke.hopper.catalog.listing.Listing;
import dev.fincke.hopper.catalog.listing.ListingRepository;
import dev.fincke.hopper.catalog.product.exception.ProductDeletionNotAllowedException;
import dev.fincke.hopper.order.item.OrderItemRepository;
import dev.fincke.hopper.platform.platform.Platform;
import dev.fincke.hopper.platform.platform.PlatformRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

// * Test Class
// Integration tests for ProductServiceImpl deletion constraints with @DataJpaTest
@DataJpaTest
@SuppressWarnings("null")
class ProductServiceImplTest
{
    // * Dependencies
    
    // Spring-injected repository for product data access
    @Autowired
    private ProductRepository productRepository;

    // Spring-injected repository for listing data access
    @Autowired
    private ListingRepository listingRepository;

    // Spring-injected repository for order item data access
    @Autowired
    private OrderItemRepository orderItemRepository;

    // Spring-injected repository for platform data access
    @Autowired
    private PlatformRepository platformRepository;

    // Service under test (manually instantiated)
    private ProductServiceImpl productService;

    // * Setup
    
    @BeforeEach
    void setUp()
    {
        // Create service instance with injected repositories
        productService = new ProductServiceImpl(productRepository, listingRepository, orderItemRepository);
    }
    
    // * Tests

    @Test
    @DisplayName("deleteProduct removes the product when no dependencies exist")
    void deleteProductWithoutDependencies()
    {
        Product product = productRepository.save(new Product("SKU-1", "Test Product", BigDecimal.TEN));
        UUID productId = product.getId();

        productService.deleteProduct(productId);

        assertThat(productRepository.existsById(productId)).isFalse();
    }

    @Test
    @DisplayName("deleteProduct throws when listings reference the product")
    void deleteProductWithListingDependency()
    {
        Product product = productRepository.save(new Product("SKU-2", "Product With Listing", BigDecimal.TEN));
        Platform platform = platformRepository.save(new Platform("eBay", "MARKETPLACE"));
        Listing listing = new Listing(product, platform, "EXT-1", "active", BigDecimal.TEN, 1);
        listingRepository.save(listing);

        assertThrows(ProductDeletionNotAllowedException.class, () -> productService.deleteProduct(product.getId()));
        assertThat(productRepository.existsById(product.getId())).isTrue();
    }
}
