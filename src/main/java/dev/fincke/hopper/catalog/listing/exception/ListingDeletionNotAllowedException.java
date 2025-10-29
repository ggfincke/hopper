package dev.fincke.hopper.catalog.listing.exception;

import dev.fincke.hopper.api.error.ConflictException;

import java.util.UUID;

// Raised when a listing cannot be removed because related order activity still exists
public class ListingDeletionNotAllowedException extends ConflictException
{
    // ID of the listing that cannot be deleted
    private final UUID listingId;

    public ListingDeletionNotAllowedException(UUID listingId, String reason)
    {
        super("Cannot delete listing " + listingId + ": " + reason);
        this.listingId = listingId;
    }

    public UUID getListingId()
    {
        return listingId;
    }
}
