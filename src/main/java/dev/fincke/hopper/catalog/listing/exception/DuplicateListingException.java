package dev.fincke.hopper.catalog.listing.exception;

import java.util.UUID;

/**
 * Exception thrown when attempting to create a listing with an external listing ID 
 * that already exists on the same platform.
 * 
 * External listing IDs must be unique per platform to prevent listing conflicts.
 */
public class DuplicateListingException extends RuntimeException 
{
    // * Attributes
    
    // the duplicate external listing ID that caused the exception
    private final String externalListingId;
    
    // the platform ID where the duplicate was found
    private final UUID platformId;
    
    // * Constructors
    
    public DuplicateListingException(UUID platformId, String externalListingId) 
    {
        super("Listing with external ID '" + externalListingId + "' already exists on platform " + platformId);
        this.platformId = platformId;
        this.externalListingId = externalListingId;
    }
    
    public DuplicateListingException(UUID platformId, String externalListingId, String message) 
    {
        super(message);
        this.platformId = platformId;
        this.externalListingId = externalListingId;
    }
    
    public DuplicateListingException(UUID platformId, String externalListingId, String message, Throwable cause) 
    {
        super(message, cause);
        this.platformId = platformId;
        this.externalListingId = externalListingId;
    }
    
    // * Getters
    
    // returns the duplicate external listing ID that caused this exception
    public String getExternalListingId() 
    {
        return externalListingId;
    }
    
    // returns the platform ID where the duplicate was found
    public UUID getPlatformId() 
    {
        return platformId;
    }
    
}