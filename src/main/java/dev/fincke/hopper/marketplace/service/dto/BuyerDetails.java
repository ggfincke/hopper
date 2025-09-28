package dev.fincke.hopper.marketplace.service.dto;

import java.util.Objects;

// * DTO
// Represents buyer identity and address information for marketplace orders.
public record BuyerDetails(String name, AddressDetails address)
{
    // * Canonical Constructor
    // Ensures buyer information is well-formed before connector submission.
    public BuyerDetails
    {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(address, "address");

        name = name.trim();
        if (name.isEmpty())
        {
            throw new IllegalArgumentException("name must not be blank");
        }
    }
}
