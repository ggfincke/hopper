package dev.fincke.hopper.marketplace.service.dto;

import java.util.Objects;

// * DTO
// Captures postal address data for downstream marketplace connectors.
public record AddressDetails(
    String line1,
    String city,
    String region,
    String postal,
    String country
)
{
    // * Canonical Constructor
    // Trims and validates basic address fields before transport.
    public AddressDetails
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
