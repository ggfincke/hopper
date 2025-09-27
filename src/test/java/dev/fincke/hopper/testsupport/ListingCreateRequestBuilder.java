package dev.fincke.hopper.testsupport;

import dev.fincke.hopper.catalog.listing.dto.ListingCreateRequest;

import java.math.BigDecimal;
import java.util.UUID;

// Builder for ListingCreateRequest payloads in tests
// Creates listing creation DTOs with realistic defaults for API endpoint testing
public final class ListingCreateRequestBuilder
{
    // * Default Test Values
    
    // Random product ID for relationship validation testing
    private UUID productId = UUID.randomUUID();
    // Random platform ID for marketplace integration testing
    private UUID platformId = UUID.randomUUID();
    // Standard external listing ID format for uniqueness constraint testing
    private String externalListingId = "LIST-1001";
    // Active status (most common listing state for business flow testing)
    private String status = "active";
    // Realistic marketplace price with proper decimal precision
    private BigDecimal price = new BigDecimal("29.99");
    // Positive quantity for inventory management testing
    private int quantityListed = 5;

    // * Factory Method
    
    // Private constructor enforces builder pattern usage
    private ListingCreateRequestBuilder()
    {
    }

    // Factory method to start builder chain for listing creation requests
    public static ListingCreateRequestBuilder listingCreateRequest()
    {
        return new ListingCreateRequestBuilder();
    }

    // * Builder Methods
    
    // Override product ID (for testing specific product relationships)
    public ListingCreateRequestBuilder withProductId(UUID productId)
    {
        this.productId = productId;
        return this;
    }

    // Override platform ID (for testing marketplace-specific behavior)
    public ListingCreateRequestBuilder withPlatformId(UUID platformId)
    {
        this.platformId = platformId;
        return this;
    }

    // Override external listing ID (for testing uniqueness constraints and duplicates)
    public ListingCreateRequestBuilder withExternalListingId(String externalListingId)
    {
        this.externalListingId = externalListingId;
        return this;
    }

    // Override status (for testing status validation and business rules)
    public ListingCreateRequestBuilder withStatus(String status)
    {
        this.status = status;
        return this;
    }

    // Override price (for testing pricing validation and decimal precision)
    public ListingCreateRequestBuilder withPrice(BigDecimal price)
    {
        this.price = price;
        return this;
    }

    // Override quantity (for testing inventory constraints and validation)
    public ListingCreateRequestBuilder withQuantityListed(int quantity)
    {
        this.quantityListed = quantity;
        return this;
    }

    // * DTO Construction
    
    // Builds ListingCreateRequest DTO for API endpoint testing
    public ListingCreateRequest build()
    {
        // Create DTO with configured values (validates all required fields)
        return new ListingCreateRequest(productId, platformId, externalListingId, status, price, quantityListed);
    }
}
