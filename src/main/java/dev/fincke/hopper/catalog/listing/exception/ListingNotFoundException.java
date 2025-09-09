package dev.fincke.hopper.catalog.listing.exception;

import java.util.UUID;

/**
 * Exception thrown when a requested listing cannot be found in the system.
 * 
 * Used for operations that require an existing listing to be present.
 */
public class ListingNotFoundException extends RuntimeException 
{
    // * Attributes
    
    // ID of the listing that was not found
    private final UUID listingId;
    
    // * Constructors
    
    public ListingNotFoundException(UUID listingId) 
    {
        super("Listing with ID " + listingId + " not found");
        this.listingId = listingId;
    }
    
    public ListingNotFoundException(UUID platformId, String externalListingId) 
    {
        super("Listing with external ID '" + externalListingId + "' not found on platform " + platformId);
        this.listingId = null;
    }
    
    public ListingNotFoundException(UUID listingId, String message, Throwable cause) 
    {
        super(message, cause);
        this.listingId = listingId;
    }
    
    // * Getters
    
    // returns the ID of the listing that was not found (may be null for external ID-based lookups)
    public UUID getListingId() 
    {
        return listingId;
    }
    
}