package dev.fincke.hopper.marketplace.client.model;

import java.util.Objects;

// * Value Object
// Captures optional media URLs while guarding against blank inputs.
public record ListingMedia(String url)
{
    // * Canonical Constructor
    // Validates the URL string so connector payloads never include empty values.
    public ListingMedia
    {
        Objects.requireNonNull(url, "url");
        if (url.isBlank())
        {
            throw new IllegalArgumentException("url must not be blank");
        }
    }
}
