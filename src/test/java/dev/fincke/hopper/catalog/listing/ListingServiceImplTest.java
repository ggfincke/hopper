package dev.fincke.hopper.catalog.listing;

import dev.fincke.hopper.catalog.product.Product;
import dev.fincke.hopper.catalog.product.ProductRepository;
import dev.fincke.hopper.catalog.listing.exception.ListingDeletionNotAllowedException;
import dev.fincke.hopper.order.item.OrderItem;
import dev.fincke.hopper.order.item.OrderItemRepository;
import dev.fincke.hopper.order.order.Order;
import dev.fincke.hopper.order.order.OrderRepository;
import dev.fincke.hopper.platform.platform.Platform;
import dev.fincke.hopper.platform.platform.PlatformRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class ListingServiceImplTest
{
    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PlatformRepository platformRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    private ListingServiceImpl listingService;

    @BeforeEach
    void setUp()
    {
        listingService = new ListingServiceImpl(listingRepository, productRepository, platformRepository, orderItemRepository);
    }

    @Test
    @DisplayName("deleteListing removes the listing when no order items reference it")
    void deleteListingWithoutDependencies()
    {
        Listing listing = createListing();
        UUID listingId = listing.getId();

        listingService.deleteListing(listingId);

        assertThat(listingRepository.existsById(listingId)).isFalse();
    }

    @Test
    @DisplayName("deleteListing throws when order items reference the listing")
    void deleteListingWithOrderItems()
    {
        Listing listing = createListing();
        Order order = createOrder(listing.getPlatform());
        orderItemRepository.save(new OrderItem(order, listing, 1, BigDecimal.TEN));

        assertThrows(ListingDeletionNotAllowedException.class, () -> listingService.deleteListing(listing.getId()));
        assertThat(listingRepository.existsById(listing.getId())).isTrue();
    }

    private Listing createListing()
    {
        Product product = productRepository.save(new Product("SKU-L", "Listing Product", BigDecimal.valueOf(19.99)));
        Platform platform = platformRepository.save(new Platform("Amazon", "MARKETPLACE"));
        Listing listing = new Listing(product, platform, "LIST-1", "active", BigDecimal.valueOf(21.99), 5);
        return listingRepository.save(listing);
    }

    private Order createOrder(Platform platform)
    {
        Order order = new Order(platform, "ORDER-1", "pending", BigDecimal.valueOf(21.99), Timestamp.from(Instant.now()));
        return orderRepository.save(order);
    }
}
