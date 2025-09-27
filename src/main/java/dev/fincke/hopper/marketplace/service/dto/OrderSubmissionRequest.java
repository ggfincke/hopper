package dev.fincke.hopper.marketplace.service.dto;

import java.util.List;
import java.util.Objects;

// * DTO
// Aggregates all data required to submit an order to a marketplace connector.
public record OrderSubmissionRequest(
    String platform,
    String sellerAccountId,
    String listingId,
    String sku,
    BuyerDetails buyer,
    List<OrderSubmissionItem> items,
    String idempotencyKey
)
{
    // * Canonical Constructor
    // Validates and normalizes high-level order submission fields.
    public OrderSubmissionRequest
    {
        Objects.requireNonNull(platform, "platform");
        Objects.requireNonNull(sellerAccountId, "sellerAccountId");
        Objects.requireNonNull(items, "items");
        Objects.requireNonNull(idempotencyKey, "idempotencyKey");

        platform = platform.trim().toUpperCase();
        sellerAccountId = sellerAccountId.trim();
        idempotencyKey = idempotencyKey.trim();

        if (platform.isEmpty())
        {
            throw new IllegalArgumentException("platform must not be blank");
        }
        if (sellerAccountId.isEmpty())
        {
            throw new IllegalArgumentException("sellerAccountId must not be blank");
        }
        if (idempotencyKey.isEmpty())
        {
            throw new IllegalArgumentException("idempotencyKey must not be blank");
        }
        if (items.isEmpty())
        {
            throw new IllegalArgumentException("items must not be empty");
        }

        if (listingId != null)
        {
            listingId = listingId.trim();
            if (listingId.isEmpty())
            {
                listingId = null;
            }
        }

        if (sku != null)
        {
            sku = sku.trim();
            if (sku.isEmpty())
            {
                sku = null;
            }
        }

        if (listingId == null && sku == null)
        {
            throw new IllegalArgumentException("either listingId or sku must be provided");
        }

        items = List.copyOf(items);
    }

    // * Helpers
    // Indicates whether this request references an existing marketplace listing.
    public boolean referencesListingId()
    {
        return listingId != null;
    }
}
