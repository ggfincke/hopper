package dev.fincke.hopper.marketplace.client.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

// * Value Object
// Normalizes monetary amounts crossing the marketplace connector boundary.
public record MoneyValue(BigDecimal amount, String currency)
{
    // * Canonical Constructor
    // Rounds to two decimals and uppercases ISO currency codes to protect downstream APIs.
    public MoneyValue
    {
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(currency, "currency");
        if (currency.isBlank())
        {
            throw new IllegalArgumentException("currency must not be blank");
        }
        amount = amount.setScale(2, RoundingMode.HALF_UP);
        currency = currency.trim().toUpperCase();
    }
}
