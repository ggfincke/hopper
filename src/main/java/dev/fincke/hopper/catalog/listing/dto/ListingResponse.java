package dev.fincke.hopper.catalog.listing.dto;

import dev.fincke.hopper.catalog.listing.Listing;

import java.math.BigDecimal;
import java.util.UUID;

// Response DTO for listing data (immutable for API responses)
public record ListingResponse(
    // listing ID
    UUID id,
    
    // product ID that this listing represents
    UUID productId,
    
    // platform ID where this listing exists
    UUID platformId,
    
    // external listing ID on the platform
    String externalListingId,
    
    // status of the listing
    String status,
    
    // price for this listing
    BigDecimal price,
    
    // quantity currently listed
    int quantityListed
) {
    // * Static Factory Methods
    
    // create response from entity
    public static ListingResponse from(Listing listing) 
    {
        return new ListingResponse(
            listing.getId(),
            listing.getProduct().getId(),
            listing.getPlatform().getId(),
            listing.getExternalListingId(),
            listing.getStatus(),
            listing.getPrice(),
            listing.getQuantityListed()
        );
    }
    
}