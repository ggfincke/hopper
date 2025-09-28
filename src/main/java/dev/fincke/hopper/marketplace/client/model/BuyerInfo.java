package dev.fincke.hopper.marketplace.client.model;

import java.util.Objects;

// * Value Object
// Holds buyer identity required by certain marketplaces for order creation.
public record BuyerInfo(String name, AddressInfo address)
{
    // * Canonical Constructor
    // Ensures name is present to avoid connector validation failures.
    public BuyerInfo
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
