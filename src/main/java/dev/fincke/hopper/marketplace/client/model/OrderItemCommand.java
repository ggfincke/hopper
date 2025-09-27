package dev.fincke.hopper.marketplace.client.model;

import java.util.Objects;

// * Command DTO
// Represents a single normalized order line for connector calls.
public record OrderItemCommand(String sku, int quantity, MoneyValue price)
{
    // * Canonical Constructor
    // Guards against missing SKU or zero quantity before hitting external APIs.
    public OrderItemCommand
    {
        Objects.requireNonNull(sku, "sku");
        Objects.requireNonNull(price, "price");
        sku = sku.trim();
        if (sku.isEmpty())
        {
            throw new IllegalArgumentException("sku must not be blank");
        }
        if (quantity < 1)
        {
            throw new IllegalArgumentException("quantity must be at least 1");
        }
    }
}
