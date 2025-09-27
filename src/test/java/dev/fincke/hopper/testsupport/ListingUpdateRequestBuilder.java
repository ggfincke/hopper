package dev.fincke.hopper.testsupport;

import dev.fincke.hopper.catalog.listing.dto.ListingUpdateRequest;

import java.math.BigDecimal;

// Builder for ListingUpdateRequest objects in unit tests
// Creates listing update DTOs with optional fields for partial update testing
public final class ListingUpdateRequestBuilder
{
    // * Default Test Values (all optional for partial updates)
    
    // External listing ID (null by default, set only when testing ID changes)
    private String externalListingId;
    // Status (null by default, set only when testing status changes)
    private String status;
    // Price (null by default, set only when testing price updates)
    private BigDecimal price;
    // Quantity (null by default, set only when testing inventory updates)
    private Integer quantityListed;

    // * Factory Method
    
    // Private constructor enforces builder pattern usage
    private ListingUpdateRequestBuilder()
    {
    }

    // Factory method to start builder chain for listing update requests
    public static ListingUpdateRequestBuilder listingUpdateRequest()
    {
        return new ListingUpdateRequestBuilder();
    }

    // * Builder Methods
    
    // Set external listing ID (for testing ID changes and uniqueness constraints)
    public ListingUpdateRequestBuilder withExternalListingId(String externalListingId)
    {
        this.externalListingId = externalListingId;
        return this;
    }

    // Set status (for testing status transitions and validation)
    public ListingUpdateRequestBuilder withStatus(String status)
    {
        this.status = status;
        return this;
    }

    // Set price (for testing price updates and decimal precision)
    public ListingUpdateRequestBuilder withPrice(BigDecimal price)
    {
        this.price = price;
        return this;
    }

    // Set quantity (for testing inventory updates and constraints)
    public ListingUpdateRequestBuilder withQuantityListed(Integer quantityListed)
    {
        this.quantityListed = quantityListed;
        return this;
    }

    // * DTO Construction
    
    // Builds ListingUpdateRequest DTO for partial update testing
    public ListingUpdateRequest build()
    {
        // Create DTO with only specified fields (supports partial updates)
        return new ListingUpdateRequest(externalListingId, status, price, quantityListed);
    }
}
