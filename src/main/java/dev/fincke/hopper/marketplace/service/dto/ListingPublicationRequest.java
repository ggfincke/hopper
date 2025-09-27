package dev.fincke.hopper.marketplace.service.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

// * DTO
// Represents the normalized data needed to publish a listing to a marketplace connector.
public record ListingPublicationRequest(
    String platform,
    String sellerAccountId,
    String sku,
    String title,
    String description,
    BigDecimal price,
    String currency,
    int quantity,
    List<String> mediaUrls
)
{
    // * Canonical Constructor
    // Performs lightweight validation and normalization before mapping to connector commands.
    public ListingPublicationRequest
    {
        Objects.requireNonNull(platform, "platform");
        Objects.requireNonNull(sellerAccountId, "sellerAccountId");
        Objects.requireNonNull(sku, "sku");
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(description, "description");
        Objects.requireNonNull(price, "price");
        Objects.requireNonNull(currency, "currency");
        Objects.requireNonNull(mediaUrls, "mediaUrls");

        platform = platform.trim().toUpperCase();
        sellerAccountId = sellerAccountId.trim();
        sku = sku.trim();
        title = title.trim();
        description = description.trim();
        currency = currency.trim().toUpperCase();

        if (platform.isEmpty())
        {
            throw new IllegalArgumentException("platform must not be blank");
        }
        if (sellerAccountId.isEmpty())
        {
            throw new IllegalArgumentException("sellerAccountId must not be blank");
        }
        if (sku.isEmpty())
        {
            throw new IllegalArgumentException("sku must not be blank");
        }
        if (title.isEmpty())
        {
            throw new IllegalArgumentException("title must not be blank");
        }
        if (description.isEmpty())
        {
            throw new IllegalArgumentException("description must not be blank");
        }
        if (quantity < 1)
        {
            throw new IllegalArgumentException("quantity must be at least 1");
        }

        price = price.setScale(2, RoundingMode.HALF_UP);
        mediaUrls = mediaUrls.stream()
            .map(url -> Objects.requireNonNull(url, "media url").trim())
            .filter(url -> !url.isEmpty())
            .toList();
    }

    // * Helpers
    // Indicates whether any media references were provided.
    public boolean hasMedia()
    {
        return !mediaUrls().isEmpty();
    }
}
