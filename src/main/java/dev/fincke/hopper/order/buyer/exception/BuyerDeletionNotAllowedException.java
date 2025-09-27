package dev.fincke.hopper.order.buyer.exception;

import java.util.UUID;

// Raised when a buyer cannot be removed because orders are still associated with the buyer
public class BuyerDeletionNotAllowedException extends RuntimeException
{
    // ID of the buyer that cannot be deleted
    private final UUID buyerId;

    public BuyerDeletionNotAllowedException(UUID buyerId)
    {
        super("Cannot delete buyer " + buyerId + " while orders reference this buyer");
        this.buyerId = buyerId;
    }

    public UUID getBuyerId()
    {
        return buyerId;
    }
}
