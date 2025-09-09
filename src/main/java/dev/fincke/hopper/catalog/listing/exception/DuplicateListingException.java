package dev.fincke.hopper.catalog.listing.exception;

import java.util.UUID;

// Exception thrown when external listing ID already exists on platform
public class DuplicateListingException extends RuntimeException 
{
    // * Attributes
    
    // duplicate external listing ID
    private final String externalListingId;
    
    // platform ID where duplicate was found
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
    
    // external listing ID
    public String getExternalListingId() 
    {
        return externalListingId;
    }
    
    // platform ID
    public UUID getPlatformId() 
    {
        return platformId;
    }
    
}