package dev.fincke.hopper.testsupport;

import dev.fincke.hopper.catalog.listing.Listing;
import dev.fincke.hopper.catalog.product.Product;
import dev.fincke.hopper.platform.platform.Platform;

import java.math.BigDecimal;
import java.util.UUID;

// Builder for Listing aggregates to simplify listing service tests
// Creates listings with realistic relationships for marketplace integration testing
public final class ListingTestBuilder
{
    // * Default Test Values
    
    // Auto-generated unique ID for each test listing
    private UUID id = UUID.randomUUID();
    // Default product entity with standard test data
    private Product product = ProductTestBuilder.product().build();
    // Default platform entity for marketplace integration testing
    private Platform platform = PlatformTestBuilder.platform().build();
    // Standard external listing ID format for uniqueness constraint testing
    private String externalListingId = "LIST-1001";
    // Active status (most common listing state for business flow testing)
    private String status = "active";
    // Realistic marketplace price with proper decimal precision
    private BigDecimal price = new BigDecimal("29.99");
    // Positive quantity for inventory availability testing
    private int quantityListed = 5;

    // * Factory Method
    
    // Private constructor enforces builder pattern usage
    private ListingTestBuilder()
    {
    }

    // Factory method to start builder chain for listing entities
    public static ListingTestBuilder listing()
    {
        return new ListingTestBuilder();
    }

    // * Builder Methods
    
    // Override default ID (useful for relationship and lookup testing)
    public ListingTestBuilder withId(UUID id)
    {
        this.id = id;
        return this;
    }

    // Override default product (for testing specific product relationships)
    public ListingTestBuilder withProduct(Product product)
    {
        this.product = product;
        return this;
    }

    // Override default platform (for testing marketplace-specific behavior)
    public ListingTestBuilder withPlatform(Platform platform)
    {
        this.platform = platform;
        return this;
    }

    // Override external listing ID (for testing uniqueness constraints and duplicates)
    public ListingTestBuilder withExternalListingId(String externalListingId)
    {
        this.externalListingId = externalListingId;
        return this;
    }

    // Override status (for testing status validation and business rules)
    public ListingTestBuilder withStatus(String status)
    {
        this.status = status;
        return this;
    }

    // Override price (for testing pricing validation and marketplace rules)
    public ListingTestBuilder withPrice(BigDecimal price)
    {
        this.price = price;
        return this;
    }

    // Override quantity (for testing inventory management and availability)
    public ListingTestBuilder withQuantityListed(int quantity)
    {
        this.quantityListed = quantity;
        return this;
    }

    // * Entity Construction
    
    // Builds Listing entity with configured relationships and values
    public Listing build()
    {
        // Create listing using main constructor (validates all required relationships)
        Listing listing = new Listing(product, platform, externalListingId, status, price, quantityListed);
        // Set ID manually (simulates database ID assignment)
        listing.setId(id);
        return listing;
    }
}
