package dev.fincke.hopper.order.address.exception;

import dev.fincke.hopper.api.error.ConflictException;

import java.util.UUID;

// Raised when removing an order address would break shipping or fulfillment expectations
public class OrderAddressDeletionNotAllowedException extends ConflictException
{
    // ID of the order address that cannot be deleted
    private final UUID addressId;

    public OrderAddressDeletionNotAllowedException(UUID addressId, String reason)
    {
        super("Cannot delete order address " + addressId + ": " + reason);
        this.addressId = addressId;
    }

    public UUID getAddressId()
    {
        return addressId;
    }
}
