package dev.fincke.hopper.marketplace.client.model;

import java.util.Objects;

// * Value Object
// Normalizes buyer addresses so marketplace connectors receive consistent casing and data.
public record AddressInfo(
    String line1,
    String city,
    String region,
    String postal,
    String country
)
{
    // * Canonical Constructor
    // Trims whitespace and uppercases country codes to align with marketplace expectations.
    public AddressInfo
    {
        Objects.requireNonNull(line1, "line1");
        Objects.requireNonNull(city, "city");
        Objects.requireNonNull(region, "region");
        Objects.requireNonNull(postal, "postal");
        Objects.requireNonNull(country, "country");

        line1 = line1.trim();
        city = city.trim();
        region = region.trim();
        postal = postal.trim();
        country = country.trim().toUpperCase();

        if (line1.isEmpty() || city.isEmpty() || region.isEmpty() || postal.isEmpty() || country.isEmpty())
        {
            throw new IllegalArgumentException("address fields must not be blank");
        }
    }
}
