package dev.fincke.hopper.catalog.listing.exception;

import java.util.UUID;

/**
 * Exception thrown when attempting to set an invalid status on a listing
 * or when a status transition is not allowed.
 * 
 * Used to enforce business rules around listing status management.
 */
public class InvalidListingStatusException extends RuntimeException 
{
    // * Attributes
    
    // ID of the listing that had the invalid status
    private final UUID listingId;
    
    // the invalid status that was attempted
    private final String attemptedStatus;
    
    // the current status of the listing
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
    
    // returns the ID of the listing that had the invalid status
    public UUID getListingId() 
    {
        return listingId;
    }
    
    // returns the invalid status that was attempted
    public String getAttemptedStatus() 
    {
        return attemptedStatus;
    }
    
    // returns the current status of the listing (may be null)
    public String getCurrentStatus() 
    {
        return currentStatus;
    }
    
}