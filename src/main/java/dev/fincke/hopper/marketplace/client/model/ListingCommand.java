package dev.fincke.hopper.marketplace.client.model;

import java.util.List;
import java.util.Objects;

// * Command DTO
// Encapsulates normalized listing data emitted by the domain layer.
public record ListingCommand(
    String platform,
    String sellerAccountId,
    String sku,
    String title,
    String description,
    MoneyValue price,
    int quantity,
    List<ListingMedia> media
)
{
    // * Canonical Constructor
    // Performs basic normalization to prevent downstream validation errors.
    public ListingCommand
    {
        Objects.requireNonNull(platform, "platform");
        Objects.requireNonNull(sellerAccountId, "sellerAccountId");
        Objects.requireNonNull(sku, "sku");
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(description, "description");
        Objects.requireNonNull(price, "price");
        Objects.requireNonNull(media, "media");

        platform = platform.trim().toUpperCase();
        sellerAccountId = sellerAccountId.trim();
        sku = sku.trim();
        title = title.trim();

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
        if (quantity < 1)
        {
            throw new IllegalArgumentException("quantity must be at least 1");
        }

        media = List.copyOf(media);
    }

    // Helps adapters quickly check whether to include media payloads.
    public boolean hasMedia()
    {
        return !media().isEmpty();
    }
}
