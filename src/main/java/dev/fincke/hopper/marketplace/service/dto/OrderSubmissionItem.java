package dev.fincke.hopper.marketplace.service.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

// * DTO
// Represents a single order line destined for a marketplace connector.
public record OrderSubmissionItem(String sku, int quantity, BigDecimal price, String currency)
{
    // * Canonical Constructor
    // Enforces minimum data requirements before mapping to connector-specific DTOs.
    public OrderSubmissionItem
    {
        Objects.requireNonNull(sku, "sku");
        Objects.requireNonNull(price, "price");
        Objects.requireNonNull(currency, "currency");

        sku = sku.trim();
        currency = currency.trim().toUpperCase();

        if (sku.isEmpty())
        {
            throw new IllegalArgumentException("sku must not be blank");
        }
        if (quantity < 1)
        {
            throw new IllegalArgumentException("quantity must be at least 1");
        }

        price = price.setScale(2, RoundingMode.HALF_UP);
    }
}
