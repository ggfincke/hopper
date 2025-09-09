package dev.fincke.hopper.catalog.listing.exception;

import java.util.UUID;

// Exception thrown for invalid listing status or disallowed status transitions
public class InvalidListingStatusException extends RuntimeException 
{
    // * Attributes
    
    // listing ID with invalid status
    private final UUID listingId;
    
    // invalid status that was attempted
    private final String attemptedStatus;
    
    // current status of the listing
    private final String currentStatus;
    
    // * Constructors
    
    public InvalidListingStatusException(UUID listingId, String attemptedStatus) 
    {
        super("Invalid status '" + attemptedStatus + "' for listing " + listingId);
        this.listingId = listingId;
        this.attemptedStatus = attemptedStatus;
        this.currentStatus = null;
    }
    
    public InvalidListingStatusException(UUID listingId, String currentStatus, String attemptedStatus) 
    {
        super("Cannot change listing " + listingId + " from status '" + currentStatus + "' to '" + attemptedStatus + "'");
        this.listingId = listingId;
        this.attemptedStatus = attemptedStatus;
        this.currentStatus = currentStatus;
    }
    
    public InvalidListingStatusException(String message) 
    {
        super(message);
        this.listingId = null;
        this.attemptedStatus = null;
        this.currentStatus = null;
    }
    
    // * Getters
    
    // listing ID
    public UUID getListingId() 
    {
        return listingId;
    }
    
    // attempted status
    public String getAttemptedStatus() 
    {
        return attemptedStatus;
    }
    
    // current status (may be null)
    public String getCurrentStatus() 
    {
        return currentStatus;
    }
    
}